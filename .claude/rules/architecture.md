---
name: architecture
description: Clean Architecture + MVI パターンの遵守ルール
severity: error
---

# アーキテクチャルール

このプロジェクトは **Clean Architecture + MVI パターン** を採用しています。このルールは、アーキテクチャの一貫性を保つために**絶対に守らなければならない**制約を定義します。

## Clean Architecture の原則

### レイヤー構造

```
feature/[feature-name]/
├── domain/
│   ├── model/          # Domain entities (純粋な Kotlin データクラス)
│   ├── repository/     # Repository インターフェース (実装は data 層)
│   └── usecase/        # UseCase (ビジネスロジック)
├── data/
│   ├── repository/     # Repository 実装
│   ├── source/         # DataSource (Remote/Local)
│   └── mapper/         # Entity ↔ Domain model 変換
└── presentation/
    ├── [Feature]ViewModel.kt   # MVI の ViewModel
    └── [Feature]UiState.kt     # 単一の不変 State
```

### 依存関係の方向

```
UI層 (Compose)
    ↓ 依存
Presentation層 (ViewModel)
    ↓ 依存
Domain層 (UseCase, Repository Interface)
    ↑ 実装
Data層 (Repository Impl, DataSource)
```

**重要な原則**:
- 内側のレイヤー（Domain）は外側のレイヤー（Data, Presentation）に依存してはいけない
- 依存は常に外側→内側の方向のみ

## 必須ルール

### 1. Domain層は他レイヤーに非依存

Domain層（`domain/`）のコードは、Data層やPresentation層のクラスをimportしてはいけません。

```kotlin
// ❌ 悪い例 - Domain層がData層に依存
package feature.sakelist.domain.usecase

import feature.sakelist.data.repository.SakeRepositoryImpl // NG
import feature.sakelist.presentation.SakeListViewModel // NG

class GetSakeListUseCase(
    private val repository: SakeRepositoryImpl // 実装クラスに依存（NG）
) {
    suspend operator fun invoke() = repository.getSakes()
}

// ✅ 良い例 - Domain層はインターフェースのみに依存
package feature.sakelist.domain.usecase

import feature.sakelist.domain.repository.SakeRepository // OK (同じDomain層)

class GetSakeListUseCase(
    private val repository: SakeRepository // インターフェースに依存（OK）
) {
    suspend operator fun invoke() = repository.getSakes()
}
```

### 2. Repository はインターフェース/実装分離

Repositoryは必ず以下のように分離してください:

- **インターフェース**: `domain/repository/` に配置
- **実装**: `data/repository/` に配置

```kotlin
// ✅ 良い例

// domain/repository/SakeRepository.kt
package feature.sakelist.domain.repository

interface SakeRepository {
    suspend fun getSakes(): Result<List<Sake>>
    suspend fun getSakeById(id: String): Result<Sake>
}

// data/repository/SakeRepositoryImpl.kt
package feature.sakelist.data.repository

import feature.sakelist.domain.repository.SakeRepository
import feature.sakelist.domain.model.Sake
import feature.sakelist.data.source.SakeRemoteDataSource
import feature.sakelist.data.source.SakeLocalDataSource

class SakeRepositoryImpl(
    private val remoteDataSource: SakeRemoteDataSource,
    private val localDataSource: SakeLocalDataSource
) : SakeRepository {
    override suspend fun getSakes(): Result<Sake> {
        // 実装
    }
}
```

### 3. ViewModel は UseCase 経由でアクセス

ViewModelはRepositoryを直接使用せず、必ずUseCaseを経由してデータにアクセスしてください。

```kotlin
// ❌ 悪い例 - ViewModelがRepositoryを直接使用
class SakeListViewModel(
    private val repository: SakeRepository // NG
) : ViewModel() {
    fun loadSakes() {
        viewModelScope.launch {
            repository.getSakes() // NG
        }
    }
}

// ✅ 良い例 - ViewModelはUseCaseを使用
class SakeListViewModel(
    private val getSakeListUseCase: GetSakeListUseCase // OK
) : ViewModel() {
    fun loadSakes() {
        viewModelScope.launch {
            getSakeListUseCase() // OK
        }
    }
}
```

**理由**:
- ビジネスロジックをUseCaseに集約
- ViewModelの責務を「UIロジック」に限定
- テストしやすい設計

## MVI パターン

### 必須要素

#### 1. 単一の不変 State

各画面には単一の `UiState` データクラスを定義してください。

```kotlin
// ✅ 良い例
data class SakeListUiState(
    val sakes: List<Sake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterCriteria: FilterCriteria? = null
)

// ❌ 悪い例 - 複数のStateを持つ
class SakeListViewModel : ViewModel() {
    val sakes = MutableStateFlow<List<Sake>>(emptyList())
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)
}
```

#### 2. Intent でUI操作を表現

ユーザーのアクションやUIイベントは、Intent（関数）として表現してください。

```kotlin
// ✅ 良い例
class SakeListViewModel(
    private val getSakeListUseCase: GetSakeListUseCase,
    private val filterSakesUseCase: FilterSakesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SakeListUiState())
    val uiState: StateFlow<SakeListUiState> = _uiState.asStateFlow()

    // Intent関数
    fun loadSakes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSakeListUseCase()
                .onSuccess { sakes ->
                    _uiState.update { it.copy(sakes = sakes, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun applyFilter(criteria: FilterCriteria) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, filterCriteria = criteria) }
            filterSakesUseCase(criteria)
                .onSuccess { sakes ->
                    _uiState.update { it.copy(sakes = sakes, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
```

#### 3. StateFlow で UI に通知

```kotlin
// ViewModel
private val _uiState = MutableStateFlow(SakeListUiState())
val uiState: StateFlow<SakeListUiState> = _uiState.asStateFlow()

// Composable
@Composable
fun SakeListScreen(viewModel: SakeListViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    SakeListContent(
        uiState = uiState,
        onLoadSakes = { viewModel.loadSakes() },
        onApplyFilter = { viewModel.applyFilter(it) }
    )
}
```

## 実装順序

Clean Architectureに従い、**内側のレイヤーから外側へ**実装してください。

### 推奨実装順序

1. **Domain層**
   - Model (Domain entities)
   - Repository インターフェース
   - UseCase

2. **Presentation層**
   - UiState
   - ViewModel (Intent関数)

3. **Data層**
   - DTO (Data Transfer Object)
   - ApiService / DAO
   - DataSource (Remote/Local)
   - Repository実装

4. **UI層**
   - Composable関数
   - デザイン仕様準拠確認

**理由**: 依存関係の方向（外側 → 内側）に逆らわないため

### 実装例

```kotlin
// Step 1: Domain層 - Model
package feature.sakelist.domain.model

data class Sake(
    val id: String,
    val name: String,
    val type: String,
    val brewery: String
)

// Step 2: Domain層 - Repository インターフェース
package feature.sakelist.domain.repository

interface SakeRepository {
    suspend fun getSakes(): Result<List<Sake>>
}

// Step 3: Domain層 - UseCase
package feature.sakelist.domain.usecase

class GetSakeListUseCase(
    private val repository: SakeRepository
) {
    suspend operator fun invoke(): Result<List<Sake>> = repository.getSakes()
}

// Step 4: Presentation層 - UiState
package feature.sakelist.presentation

data class SakeListUiState(
    val sakes: List<Sake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Step 5: Presentation層 - ViewModel
package feature.sakelist.presentation

class SakeListViewModel(
    private val getSakeListUseCase: GetSakeListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SakeListUiState())
    val uiState: StateFlow<SakeListUiState> = _uiState.asStateFlow()

    fun loadSakes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSakeListUseCase()
                .onSuccess { sakes ->
                    _uiState.update { it.copy(sakes = sakes, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}

// Step 6: Data層 - DTO
package feature.sakelist.data.model

@Serializable
data class SakeDto(
    val id: String,
    val name: String,
    val type: String,
    val brewery: String
)

// Step 7: Data層 - ApiService
package feature.sakelist.data.source

interface SakeApiService {
    suspend fun getSakes(): List<SakeDto>
}

// Step 8: Data層 - Repository実装
package feature.sakelist.data.repository

class SakeRepositoryImpl(
    private val apiService: SakeApiService
) : SakeRepository {
    override suspend fun getSakes(): Result<List<Sake>> = runCatching {
        apiService.getSakes().map { it.toDomain() }
    }
}

// Step 9: UI層 - Composable
@Composable
fun SakeListScreen(viewModel: SakeListViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSakes()
    }

    SakeListContent(uiState = uiState)
}
```

## 変更影響範囲の確認

データ構造やインターフェースを変更する場合、以下の影響範囲を必ず確認してください:

### ドメインモデル変更時

影響範囲:
- Repository インターフェース
- Repository 実装
- UseCase
- ViewModel
- UI (Composable)

```kotlin
// Sakeモデルに新しいプロパティを追加
data class Sake(
    val id: String,
    val name: String,
    val type: String,
    val brewery: String,
    val rating: Double // 新規追加
)

// 影響範囲:
// 1. Repository インターフェース（変更不要の場合もあり）
// 2. Repository 実装（DTOからのマッピング）
// 3. UseCase（変更不要の場合もあり）
// 4. ViewModel（UiStateに反映）
// 5. UI（表示ロジック追加）
```

### API パラメータ変更時

影響範囲:
- ApiService
- Repository実装
- **バックエンド側**

```kotlin
// フィルターパラメータを追加
interface SakeApiService {
    suspend fun getSakes(
        typeId: String? = null,
        breweryId: String? = null,
        minRating: Double? = null // 新規追加
    ): List<SakeDto>
}

// 影響範囲:
// 1. ApiService（パラメータ追加）
// 2. Repository実装（新パラメータを渡す）
// 3. バックエンド側（APIエンドポイント対応）
```

### Intent 変更時

影響範囲:
- ViewModel の Intent ハンドラー
- UI の Intent 発行箇所

```kotlin
// 新しいIntent関数を追加
class SakeListViewModel {
    fun sortByRating() { // 新規Intent
        viewModelScope.launch {
            val sorted = _uiState.value.sakes.sortedByDescending { it.rating }
            _uiState.update { it.copy(sakes = sorted) }
        }
    }
}

// 影響範囲:
// 1. ViewModel（Intent関数追加）
// 2. UI（Intentを発行するボタン追加）
```

## DI モジュール構成

Koinモジュールは、レイヤーごとに分割してください。

```kotlin
// Domain層モジュール
val domainModule = module {
    factory { GetSakeListUseCase(get()) }
    factory { FilterSakesUseCase(get()) }
}

// Data層モジュール
val dataModule = module {
    single<SakeRepository> { SakeRepositoryImpl(get(), get()) }
    single { SakeRemoteDataSource(get()) }
    single { SakeLocalDataSource(get()) }
}

// Presentation層モジュール
val presentationModule = module {
    viewModel { SakeListViewModel(get()) }
}

// 全体のモジュール
val appModule = listOf(
    domainModule,
    dataModule,
    presentationModule,
    platformModule // expect/actual
)
```

## まとめ

- Domain層は他レイヤーに非依存
- Repository はインターフェース/実装分離
- ViewModel は UseCase 経由でアクセス
- MVI パターン: 単一不変State、Intent、StateFlow
- 実装順序: Domain → Presentation → Data → UI
- 変更時は影響範囲を必ず確認
