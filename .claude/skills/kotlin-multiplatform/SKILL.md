---
name: kotlin-multiplatform
description: Kotlin Multiplatform開発のベストプラクティス
---

# Kotlin Multiplatform スキル

## 使用タイミング

KMP プロジェクトでコードを書く際に常に参照してください。

## null 安全性の徹底

### 絶対禁止

- `!!` (非 null アサーション) の使用を完全に禁止します
- 全角括弧 `（）` の使用を完全に禁止します

### 推奨パターン

- null チェックは `?.` (セーフコール) や `?:` (Elvis 演算子) を使用
- null 許容型の扱いは、`let`、`also`、`run` などのスコープ関数を活用

```kotlin
// ❌ 悪い例
val user = getUserOrNull()!!
val name = user.name

// ✅ 良い例
val name = getUserOrNull()?.name ?: "Unknown"

// ✅ 良い例（スコープ関数）
getUserOrNull()?.let { user ->
    println("User name: ${user.name}")
} ?: println("User not found")
```

## プラットフォーム分離の原則

### expect/actual は最小限に

- DI とインターフェースを優先してください
- expect/actual は以下の場合のみ使用:
  - データベースドライバ生成
  - HTTP エンジン設定
  - プラットフォーム固有のユーティリティ

### commonMain に最大限のロジックを配置

- ViewModels、UseCases、Repository インターフェースは全て共通コード
- プラットフォーム固有コードは androidMain/iosMain のみ

```kotlin
// commonMain - Repository インターフェース
interface UserRepository {
    suspend fun getUser(id: String): Result<User>
}

// commonMain - UseCase
class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: String) = repository.getUser(id)
}

// expect/actual でプラットフォームモジュールを分離
expect val platformModule: Module

// androidMain
actual val platformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single<HttpClient> { httpClient() }
}

// iosMain
actual val platformModule = module {
    single { DatabaseDriverFactory() }
    single<HttpClient> { httpClient() }
}
```

## Clean Architecture の遵守

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

### 重要な制約

- Domain 層は Data 層や Presentation 層に依存してはいけません
- Repository は必ずインターフェース (domain/) と実装 (data/) を分離
- ViewModel は UseCase を通してのみデータにアクセス

## 型安全性の徹底

- 明示的な型宣言を優先してください (型推論に頼りすぎない)
- `Any` 型の使用は最小限に、必要な場合は `sealed class` や `sealed interface` を検討
- ジェネリクスを適切に活用してください

```kotlin
// ❌ 悪い例
fun processData(data: Any) {
    when (data) {
        is String -> println(data)
        is Int -> println(data)
    }
}

// ✅ 良い例
sealed interface Data {
    data class Text(val value: String) : Data
    data class Number(val value: Int) : Data
}

fun processData(data: Data) {
    when (data) {
        is Data.Text -> println(data.value)
        is Data.Number -> println(data.value)
    }
}
```

## Coroutine と Flow の使用

- suspend 関数は `Dispatchers.IO` で実行 (共通コードで直接使用可能)
- UI State の管理は `StateFlow` を使用
- `Flow` の変換は `map`、`flatMapLatest`、`combine` などを活用
- `viewModelScope` でライフサイクルに応じたキャンセルを実現

```kotlin
// 良い例
class HomeViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserUseCase()
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}
```

## データクラスとイミュータビリティ

- データクラスは `data class` を使用し、全てのプロパティを `val` で宣言
- State の更新は `copy()` を使用して新しいインスタンスを生成
- コレクションは `List`、`Set`、`Map` を使用 (mutable 版は避ける)

```kotlin
// ✅ 良い例
data class HomeUiState(
    val sakes: List<Sake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 更新時
_uiState.update { it.copy(sakes = newSakes, isLoading = false) }

// ❌ 悪い例
data class HomeUiState(
    var sakes: MutableList<Sake> = mutableListOf(),
    var isLoading: Boolean = false,
    var error: String? = null
)
```

## Koin DI の使用

```kotlin
// commonMain - 共通モジュール定義
val featureModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory { GetUserUseCase(get()) }
    viewModel { HomeViewModel(get()) }
}

// expect/actual でプラットフォームモジュールを分離
expect val platformModule: Module

// androidMain
actual val platformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single<HttpClient> { httpClient() }
}

// iosMain
actual val platformModule = module {
    single { DatabaseDriverFactory() }
    single<HttpClient> { httpClient() }
}
```

## MVI パターン

このプロジェクトは MVI (Model-View-Intent) パターンを採用しています：

```kotlin
// 単一の不変 State
data class SakeListUiState(
    val sakes: List<Sake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ViewModel
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
```

## 実装順序

Clean Architectureに従い、内側のレイヤーから外側へ実装:

1. **Domain層**: Model → Repository インターフェース → UseCase
2. **Presentation層**: Intent → UiState → ViewModel
3. **Data層**: DTO → ApiService → DataSource → Repository実装
4. **UI層**: Composable関数 → デザイン仕様準拠確認

**理由**: 依存関係の方向(外側 → 内側)に逆らわないため

## 変更影響範囲の確認

データ構造やインターフェースを変更する場合、影響範囲を確認:

- **ドメインモデル変更時**: Repository インターフェース/実装、UseCase、ViewModel、UI
- **API パラメータ変更時**: ApiService、Repository実装、バックエンド側
- **Intent 変更時**: ViewModel の Intent ハンドラー、UI の Intent 発行箇所
