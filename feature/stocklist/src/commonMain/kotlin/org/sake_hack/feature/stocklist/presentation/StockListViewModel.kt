package org.sake_hack.feature.stocklist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sake_hack.core.common.error.AppError
import org.sake_hack.core.common.error.toUserMessage
import org.sake_hack.feature.stocklist.domain.validation.StockValidator
import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditField
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockFieldUpdate
import org.sake_hack.feature.stocklist.domain.usecase.CreateStockUseCase
import org.sake_hack.feature.stocklist.domain.usecase.GetStockByIdUseCase
import org.sake_hack.feature.stocklist.domain.usecase.GetStockPageUseCase
import org.sake_hack.feature.stocklist.domain.usecase.UpdateStockUseCase
import org.sake_hack.feature.stocklist.domain.usecase.UploadStockImageUseCase

/**
 * 在庫一覧ViewModel
 *
 * @property getStockPageUseCase 在庫ページ取得UseCase
 * @property getStockByIdUseCase 在庫詳細取得UseCase
 * @property createStockUseCase 在庫作成UseCase
 * @property updateStockUseCase 在庫更新UseCase
 * @property uploadStockImageUseCase 在庫画像アップロードUseCase
 */
class StockListViewModel(
    private val getStockPageUseCase: GetStockPageUseCase,
    private val getStockByIdUseCase: GetStockByIdUseCase,
    private val createStockUseCase: CreateStockUseCase,
    private val updateStockUseCase: UpdateStockUseCase,
    private val uploadStockImageUseCase: UploadStockImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockListUiState())
    val uiState: StateFlow<StockListUiState> = _uiState.asStateFlow()

    init {
        loadInitialPage()
    }

    /**
     * Intentを処理
     */
    fun handleIntent(intent: StockListIntent) {
        when (intent) {
            // データロード
            StockListIntent.LoadStockList -> loadInitialPage()
            StockListIntent.LoadNextPage -> loadNextPage()
            StockListIntent.Refresh -> refresh()

            // フィルター
            StockListIntent.OpenFilterDialog -> openFilterDialog()
            StockListIntent.CloseFilterDialog -> closeFilterDialog()
            is StockListIntent.UpdateNameFilter -> updateNameFilter(intent.name)
            is StockListIntent.ToggleCategoryFilter -> toggleCategoryFilter(intent.category)
            is StockListIntent.UpdateRegionFilter -> updateRegionFilter(intent.region)
            StockListIntent.ApplyFilter -> applyFilter()
            StockListIntent.ClearFilter -> clearFilter()

            // ソート
            StockListIntent.OpenSortMenu -> openSortMenu()
            StockListIntent.CloseSortMenu -> closeSortMenu()
            is StockListIntent.ApplySort -> applySort(intent.sortCriteria)

            // 詳細
            is StockListIntent.OpenStockDetail -> openStockDetail(intent.stock)
            StockListIntent.CloseDetailDialog -> closeDetailDialog()

            // 編集
            StockListIntent.StartEdit -> startEdit()
            StockListIntent.CancelEdit -> cancelEdit()
            is StockListIntent.UpdateEditField -> updateEditField(intent.update)
            StockListIntent.SaveEdit -> saveEdit()

            // 追加
            StockListIntent.OpenCreateDialog -> openCreateDialog()
            StockListIntent.CancelCreate -> cancelCreate()
            is StockListIntent.UpdateCreateField -> updateCreateField(intent.update)
            StockListIntent.SaveCreate -> saveCreate()

            // 画像
            StockListIntent.OpenImagePicker -> openImagePicker()
            StockListIntent.CloseImagePicker -> closeImagePicker()
            is StockListIntent.SelectImageSource -> selectImageSource(intent.source)
            is StockListIntent.OnImageSelected -> onImageSelected(intent.imageData)
            is StockListIntent.OnImagePickerError -> onImagePickerError(intent.error)
            StockListIntent.CancelCrop -> cancelCrop()
            is StockListIntent.CropImage -> cropImage(intent.croppedData)
        }
    }

    // ===== データロード =====

    /**
     * 初期ページをロード
     */
    private fun loadInitialPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInitialLoading = true, error = null) }

            getStockPageUseCase(
                offset = 0,
                limit = _uiState.value.limit,
                filterCriteria = _uiState.value.filterCriteria,
                sortCriteria = _uiState.value.sortCriteria
            )
                .onSuccess { pageResult ->
                    _uiState.update {
                        it.copy(
                            displayedStocks = pageResult.items,
                            currentOffset = pageResult.offset,
                            totalCount = pageResult.total,
                            isInitialLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    val appError = if (error is AppError) {
                        error
                    } else {
                        AppError.Unknown(
                            errorMessage = error.message ?: "在庫の取得に失敗しました",
                            errorCause = error
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isInitialLoading = false,
                            error = appError
                        )
                    }
                }
        }
    }

    /**
     * 次のページをロード(ページネーション)
     */
    private fun loadNextPage() {
        // すでに読み込み中、または次のページがない場合は何もしない
        if (_uiState.value.isLoadingMore || !_uiState.value.hasNextPage()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextOffset = _uiState.value.currentOffset + _uiState.value.limit

            getStockPageUseCase(
                offset = nextOffset,
                limit = _uiState.value.limit,
                filterCriteria = _uiState.value.filterCriteria,
                sortCriteria = _uiState.value.sortCriteria
            )
                .onSuccess { pageResult ->
                    _uiState.update {
                        it.copy(
                            displayedStocks = it.displayedStocks + pageResult.items,
                            currentOffset = nextOffset,
                            totalCount = pageResult.total,
                            isLoadingMore = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    val appError = if (error is AppError) {
                        error
                    } else {
                        AppError.Unknown(
                            errorMessage = error.message ?: "追加読み込みに失敗しました",
                            errorCause = error
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = appError
                        )
                    }
                }
        }
    }

    /**
     * リフレッシュ
     */
    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            getStockPageUseCase(
                offset = 0,
                limit = _uiState.value.limit,
                filterCriteria = _uiState.value.filterCriteria,
                sortCriteria = _uiState.value.sortCriteria
            )
                .onSuccess { pageResult ->
                    _uiState.update {
                        it.copy(
                            displayedStocks = pageResult.items,
                            currentOffset = pageResult.offset,
                            totalCount = pageResult.total,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    val appError = if (error is AppError) {
                        error
                    } else {
                        AppError.Unknown(
                            errorMessage = error.message ?: "リフレッシュに失敗しました",
                            errorCause = error
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = appError
                        )
                    }
                }
        }
    }

    // ===== フィルター =====

    /**
     * フィルターダイアログを開く
     */
    private fun openFilterDialog() {
        _uiState.update {
            it.copy(
                isFilterDialogOpen = true,
                tempFilterCriteria = it.filterCriteria
            )
        }
    }

    /**
     * フィルターダイアログを閉じる
     */
    private fun closeFilterDialog() {
        _uiState.update {
            it.copy(
                isFilterDialogOpen = false,
                tempFilterCriteria = it.filterCriteria
            )
        }
    }

    /**
     * 酒名フィルターを更新
     */
    private fun updateNameFilter(name: String?) {
        _uiState.update {
            it.copy(
                tempFilterCriteria = it.tempFilterCriteria.copy(name = name)
            )
        }
    }

    /**
     * 種類フィルターをトグル
     */
    private fun toggleCategoryFilter(category: String) {
        _uiState.update {
            val currentCategories = it.tempFilterCriteria.categories
            val newCategories = if (currentCategories.contains(category)) {
                currentCategories - category
            } else {
                currentCategories + category
            }
            it.copy(
                tempFilterCriteria = it.tempFilterCriteria.copy(categories = newCategories)
            )
        }
    }

    /**
     * 産地フィルターを更新
     */
    private fun updateRegionFilter(region: String?) {
        _uiState.update {
            it.copy(
                tempFilterCriteria = it.tempFilterCriteria.copy(region = region)
            )
        }
    }

    /**
     * フィルターを適用
     */
    private fun applyFilter() {
        _uiState.update {
            it.copy(
                filterCriteria = it.tempFilterCriteria,
                isFilterDialogOpen = false
            )
        }
        loadInitialPage()
    }

    /**
     * フィルターをクリア
     */
    private fun clearFilter() {
        _uiState.update {
            it.copy(
                filterCriteria = org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria(),
                tempFilterCriteria = org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria(),
                isFilterDialogOpen = false
            )
        }
        loadInitialPage()
    }

    // ===== ソート =====

    /**
     * ソートメニューを開く
     */
    private fun openSortMenu() {
        _uiState.update { it.copy(isSortMenuOpen = true) }
    }

    /**
     * ソートメニューを閉じる
     */
    private fun closeSortMenu() {
        _uiState.update { it.copy(isSortMenuOpen = false) }
    }

    /**
     * ソートを適用
     */
    private fun applySort(sortCriteria: org.sake_hack.feature.stocklist.domain.model.StockSortCriteria) {
        _uiState.update {
            it.copy(
                sortCriteria = sortCriteria,
                isSortMenuOpen = false
            )
        }
        loadInitialPage()
    }

    // ===== 詳細 =====

    /**
     * 在庫詳細ダイアログを開く
     */
    private fun openStockDetail(stock: Stock) {
        _uiState.update {
            it.copy(
                selectedStock = stock,
                isDetailDialogOpen = true
            )
        }
    }

    /**
     * 在庫詳細ダイアログを閉じる
     */
    private fun closeDetailDialog() {
        _uiState.update {
            it.copy(
                selectedStock = null,
                isDetailDialogOpen = false,
                isEditMode = false,
                editingStock = null,
                validationErrors = emptyMap()
            )
        }
    }

    // ===== 編集 =====

    /**
     * 編集モードを開始
     */
    private fun startEdit() {
        val selectedStock = _uiState.value.selectedStock ?: return

        val editRequest = StockEditRequest(
            name = selectedStock.name,
            kana = selectedStock.kana,
            mainCategory = selectedStock.mainCategory,
            subCategory = selectedStock.subCategory,
            region = selectedStock.region,
            abv = selectedStock.abv,
            initialVolume = selectedStock.initialVolume,
            remainingVolumePercent = selectedStock.remainingVolumePercent,
            purchasePrice = selectedStock.purchasePrice,
            notes = selectedStock.notes,
            imageUrl = selectedStock.imageUrl
        )

        _uiState.update {
            it.copy(
                isEditMode = true,
                editingStock = editRequest,
                validationErrors = emptyMap()
            )
        }
    }

    /**
     * 編集をキャンセル
     */
    private fun cancelEdit() {
        _uiState.update {
            it.copy(
                isEditMode = false,
                editingStock = null,
                validationErrors = emptyMap()
            )
        }
    }

    /**
     * 編集フィールドを更新（型安全）
     */
    private fun updateEditField(update: StockFieldUpdate) {
        val currentEdit = _uiState.value.editingStock ?: return

        val updatedEdit = when (update) {
            is StockFieldUpdate.UpdateName -> currentEdit.copy(name = update.value)
            is StockFieldUpdate.UpdateKana -> currentEdit.copy(kana = update.value)
            is StockFieldUpdate.UpdateMainCategory -> currentEdit.copy(mainCategory = update.value)
            is StockFieldUpdate.UpdateSubCategory -> currentEdit.copy(subCategory = update.value)
            is StockFieldUpdate.UpdateRegion -> currentEdit.copy(region = update.value)
            is StockFieldUpdate.UpdateAbv -> currentEdit.copy(abv = update.value)
            is StockFieldUpdate.UpdateInitialVolume -> currentEdit.copy(initialVolume = update.value)
            is StockFieldUpdate.UpdateRemainingVolume -> currentEdit.copy(remainingVolumePercent = update.value)
            is StockFieldUpdate.UpdatePurchasePrice -> currentEdit.copy(purchasePrice = update.value)
            is StockFieldUpdate.UpdateNotes -> currentEdit.copy(notes = update.value)
            is StockFieldUpdate.UpdateImageUrl -> currentEdit.copy(imageUrl = update.value)
        }

        _uiState.update { it.copy(editingStock = updatedEdit) }
    }

    /**
     * 編集を保存
     */
    private fun saveEdit() {
        val editingStock = _uiState.value.editingStock ?: return
        val selectedStockId = _uiState.value.selectedStock?.id ?: return

        // バリデーション
        val validationErrors = StockValidator.validateStockEdit(editingStock)
        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = validationErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, validationErrors = emptyMap()) }

            updateStockUseCase(selectedStockId, editingStock)
                .onSuccess { updatedStock ->
                    _uiState.update {
                        it.copy(
                            selectedStock = updatedStock,
                            isEditMode = false,
                            editingStock = null,
                            isSaving = false,
                            successMessage = "保存しました"
                        )
                    }

                    // リストを再読み込み
                    loadInitialPage()
                }
                .onFailure { error ->
                    val appError = if (error is AppError) {
                        error
                    } else {
                        AppError.Unknown(
                            errorMessage = error.message ?: "保存に失敗しました",
                            errorCause = error
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = appError
                        )
                    }
                }
        }
    }

    // ===== 追加 =====

    /**
     * 追加ダイアログを開く
     */
    private fun openCreateDialog() {
        val initialRequest = StockEditRequest(
            name = "",
            kana = "",
            mainCategory = "",
            subCategory = null,
            region = null,
            abv = 0,
            initialVolume = 0,
            remainingVolumePercent = 100,
            purchasePrice = 0,
            notes = null,
            imageUrl = null
        )

        _uiState.update {
            it.copy(
                isCreateDialogOpen = true,
                creatingStock = initialRequest,
                createValidationErrors = emptyMap()
            )
        }
    }

    /**
     * 追加をキャンセル
     */
    private fun cancelCreate() {
        _uiState.update {
            it.copy(
                isCreateDialogOpen = false,
                creatingStock = null,
                createValidationErrors = emptyMap()
            )
        }
    }

    /**
     * 追加フィールドを更新（型安全）
     */
    private fun updateCreateField(update: StockFieldUpdate) {
        val currentCreate = _uiState.value.creatingStock ?: return

        val updatedCreate = when (update) {
            is StockFieldUpdate.UpdateName -> currentCreate.copy(name = update.value)
            is StockFieldUpdate.UpdateKana -> currentCreate.copy(kana = update.value)
            is StockFieldUpdate.UpdateMainCategory -> currentCreate.copy(mainCategory = update.value)
            is StockFieldUpdate.UpdateSubCategory -> currentCreate.copy(subCategory = update.value)
            is StockFieldUpdate.UpdateRegion -> currentCreate.copy(region = update.value)
            is StockFieldUpdate.UpdateAbv -> currentCreate.copy(abv = update.value)
            is StockFieldUpdate.UpdateInitialVolume -> currentCreate.copy(initialVolume = update.value)
            is StockFieldUpdate.UpdateRemainingVolume -> currentCreate.copy(remainingVolumePercent = update.value)
            is StockFieldUpdate.UpdatePurchasePrice -> currentCreate.copy(purchasePrice = update.value)
            is StockFieldUpdate.UpdateNotes -> currentCreate.copy(notes = update.value)
            is StockFieldUpdate.UpdateImageUrl -> currentCreate.copy(imageUrl = update.value)
        }

        _uiState.update { it.copy(creatingStock = updatedCreate) }
    }

    /**
     * 追加を保存
     */
    private fun saveCreate() {
        val creatingStock = _uiState.value.creatingStock ?: return

        // バリデーション
        val validationErrors = StockValidator.validateStockEdit(creatingStock)
        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(createValidationErrors = validationErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, createValidationErrors = emptyMap()) }

            createStockUseCase(creatingStock)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCreateDialogOpen = false,
                            creatingStock = null,
                            isCreating = false,
                            successMessage = "追加しました"
                        )
                    }

                    // リストを再読み込み
                    loadInitialPage()
                }
                .onFailure { error ->
                    val appError = if (error is AppError) {
                        error
                    } else {
                        AppError.Unknown(
                            errorMessage = error.message ?: "追加に失敗しました",
                            errorCause = error
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            error = appError
                        )
                    }
                }
        }
    }

    // ===== 画像 =====

    /**
     * 画像ピッカーを開く
     */
    private fun openImagePicker() {
        _uiState.update { it.copy(isImagePickerOpen = true) }
    }

    /**
     * 画像ピッカーを閉じる
     */
    private fun closeImagePicker() {
        _uiState.update { it.copy(isImagePickerOpen = false) }
    }

    /**
     * 画像ソースを選択（UI層でLauncherを起動する）
     */
    private fun selectImageSource(source: ImageSource) {
        _uiState.update {
            it.copy(
                isImagePickerOpen = false,
                selectedImageSource = source
            )
        }
    }

    /**
     * 画像選択完了（UI層のLauncherから呼ばれる）
     */
    private fun onImageSelected(imageData: ByteArray) {
        _uiState.update {
            it.copy(
                selectedImageSource = null,
                isCropMode = true,
                selectedImageData = imageData
            )
        }
    }

    /**
     * 画像選択エラー
     */
    private fun onImagePickerError(error: String) {
        _uiState.update {
            it.copy(
                selectedImageSource = null,
                error = AppError.Unknown(
                    errorMessage = error,
                    errorCause = null
                )
            )
        }
    }

    /**
     * トリミングをキャンセル
     */
    private fun cancelCrop() {
        _uiState.update {
            it.copy(
                isCropMode = false,
                selectedImageData = null
            )
        }
    }

    /**
     * 画像をトリミング
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun cropImage(croppedData: ByteArray) {
        // 画像サイズバリデーション
        val sizeError = StockValidator.validateImageSize(croppedData)
        if (sizeError != null) {
            _uiState.update {
                it.copy(
                    error = AppError.Unknown(
                        errorMessage = sizeError,
                        errorCause = null
                    ),
                    isCropMode = false
                )
            }
            return
        }

        val currentState = _uiState.value

        // 編集モードの場合
        if (currentState.isEditMode && currentState.selectedStock != null) {
            viewModelScope.launch {
                uploadStockImageUseCase(currentState.selectedStock.id, croppedData)
                    .onSuccess { imageUrl ->
                        // 編集中の在庫の画像URLを更新
                        currentState.editingStock?.let { editingStock ->
                            _uiState.update {
                                it.copy(
                                    editingStock = editingStock.copy(imageUrl = imageUrl),
                                    isCropMode = false,
                                    selectedImageData = null
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        val appError = if (error is AppError) {
                            error
                        } else {
                            AppError.Unknown(
                                errorMessage = error.message ?: "画像のアップロードに失敗しました",
                                errorCause = error
                            )
                        }
                        _uiState.update {
                            it.copy(
                                error = appError,
                                isCropMode = false,
                                selectedImageData = null
                            )
                        }
                    }
            }
        }
        // 追加モードの場合
        else if (currentState.isCreateDialogOpen && currentState.creatingStock != null) {
            // 追加モード時は画像データを一時保存し、保存時にアップロード
            // 現時点では画像データをBase64エンコードしてURLとして保存
            val imageUrl = "data:image/jpeg;base64,${Base64.encode(croppedData)}"
            _uiState.update {
                it.copy(
                    creatingStock = currentState.creatingStock.copy(imageUrl = imageUrl),
                    isCropMode = false,
                    selectedImageData = null
                )
            }
        }
    }

    /**
     * エラーをクリア
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * 成功メッセージをクリア
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
