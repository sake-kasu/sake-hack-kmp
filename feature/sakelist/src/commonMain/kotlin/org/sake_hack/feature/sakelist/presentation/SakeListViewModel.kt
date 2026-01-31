package org.sake_hack.feature.sakelist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sake_hack.core.common.error.AppError
import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.usecase.GetSakePageUseCase
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * 酒一覧画面のViewModel（MVIパターン）
 * UI状態を管理し、ユーザーインテントを処理
 * ページネーション機能を実装
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("SakeListViewModel")
class SakeListViewModel(
    private val getSakePageUseCase: GetSakePageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SakeListUiState())
    val uiState: StateFlow<SakeListUiState> = _uiState.asStateFlow()

    init {
        handleIntent(SakeListIntent.LoadSakeList)
    }

    /**
     * ユーザーインテントを処理
     */
    fun handleIntent(intent: SakeListIntent) {
        when (intent) {
            is SakeListIntent.LoadSakeList -> loadInitialPage()
            is SakeListIntent.LoadNextPage -> loadNextPage()
            is SakeListIntent.Refresh -> loadInitialPage()

            // Filter intents
            is SakeListIntent.OpenFilterDialog -> openFilterDialog()
            is SakeListIntent.CloseFilterDialog -> closeFilterDialog()
            is SakeListIntent.UpdateSakeNameFilter -> updateSakeNameFilter(intent.sakeName)
            is SakeListIntent.ToggleSakeTypeFilter -> toggleSakeTypeFilter(intent.sakeType)
            is SakeListIntent.UpdateRegionFilter -> updateRegionFilter(intent.region)
            is SakeListIntent.ApplyFilter -> applyFilter()
            is SakeListIntent.ClearFilter -> clearFilter()

            // Sort intents
            is SakeListIntent.OpenSortMenu -> openSortMenu()
            is SakeListIntent.CloseSortMenu -> closeSortMenu()
            is SakeListIntent.ApplySort -> applySort(intent.field, intent.ascending)

            // Detail intents
            is SakeListIntent.OpenSakeDetail -> openSakeDetail(intent.sake)
            is SakeListIntent.CloseDetailDialog -> closeDetailDialog()
        }
    }

    /**
     * 初期ページをロード
     */
    private fun loadInitialPage() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isInitialLoading = true,
                    error = null,
                    currentOffset = 0,
                    displayedSake = emptyList()
                )
            }

            getSakePageUseCase(
                offset = 0,
                limit = 20,
                filterCriteria = _uiState.value.filterCriteria,
                sortCriteria = _uiState.value.sortCriteria
            )
            .onSuccess { result ->
                _uiState.update { state ->
                    state.copy(
                        displayedSake = result.items,
                        currentOffset = result.offset,
                        totalCount = result.total,
                        isInitialLoading = false
                    )
                }
            }
            .onFailure { exception ->
                val appError = if (exception is AppError) {
                    exception
                } else {
                    AppError.Unknown(
                        errorMessage = exception.message ?: "Unknown error",
                        errorCause = exception
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
     * 次ページをロード(重複リクエスト防止付き)
     */
    private fun loadNextPage() {
        val currentState = _uiState.value

        // 重複リクエスト防止
        if (currentState.isLoadingMore || !currentState.hasNextPage()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextOffset = currentState.currentOffset + currentState.limit

            getSakePageUseCase(
                offset = nextOffset,
                limit = 20,
                filterCriteria = currentState.filterCriteria,
                sortCriteria = currentState.sortCriteria
            )
            .onSuccess { result ->
                _uiState.update { state ->
                    state.copy(
                        displayedSake = state.displayedSake + result.items,
                        currentOffset = result.offset,
                        totalCount = result.total,
                        isLoadingMore = false
                    )
                }
            }
            .onFailure { exception ->
                val appError = if (exception is AppError) {
                    exception
                } else {
                    AppError.Unknown(
                        errorMessage = exception.message ?: "Unknown error",
                        errorCause = exception
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

    private fun openFilterDialog() {
        _uiState.update { it.copy(isFilterDialogOpen = true) }
    }

    private fun closeFilterDialog() {
        _uiState.update { it.copy(isFilterDialogOpen = false) }
    }

    private fun updateSakeNameFilter(sakeName: String?) {
        _uiState.update { state ->
            state.copy(
                filterCriteria = state.filterCriteria.copy(sakeName = sakeName)
            )
        }
    }

    private fun toggleSakeTypeFilter(sakeType: String) {
        val currentTypes = _uiState.value.filterCriteria.sakeTypes
        val newTypes = if (currentTypes.contains(sakeType)) {
            currentTypes - sakeType
        } else {
            currentTypes + sakeType
        }
        _uiState.update { state ->
            state.copy(
                filterCriteria = state.filterCriteria.copy(sakeTypes = newTypes)
            )
        }
    }

    private fun updateRegionFilter(region: String?) {
        _uiState.update { state ->
            state.copy(
                filterCriteria = state.filterCriteria.copy(region = region)
            )
        }
    }

    private fun applyFilter() {
        _uiState.update { it.copy(isFilterDialogOpen = false) }
        // フィルター変更時は初期ページから再取得
        loadInitialPage()
    }

    private fun clearFilter() {
        _uiState.update { state ->
            state.copy(
                filterCriteria = org.sake_hack.feature.sakelist.domain.model.FilterCriteria(),
                isFilterDialogOpen = false
            )
        }
        loadInitialPage()
    }

    private fun openSortMenu() {
        _uiState.update { it.copy(isSortMenuOpen = true) }
    }

    private fun closeSortMenu() {
        _uiState.update { it.copy(isSortMenuOpen = false) }
    }

    private fun applySort(field: org.sake_hack.feature.sakelist.domain.model.SakeSortField, ascending: Boolean) {
        _uiState.update { state ->
            state.copy(
                sortCriteria = org.sake_hack.feature.sakelist.domain.model.SakeSortCriteria(
                    field = field,
                    ascending = ascending
                ),
                isSortMenuOpen = false
            )
        }
        loadInitialPage()
    }

    private fun openSakeDetail(sake: Sake) {
        _uiState.update {
            it.copy(selectedSake = sake, isDetailDialogOpen = true)
        }
    }

    private fun closeDetailDialog() {
        _uiState.update {
            it.copy(selectedSake = null, isDetailDialogOpen = false)
        }
    }
}
