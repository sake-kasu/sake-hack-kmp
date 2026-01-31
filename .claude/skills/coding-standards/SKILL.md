---
name: coding-standards
description: Kotlin Multiplatform、Compose、およびAndroid/iOS開発のためのコーディング標準とベストプラクティス
---

# コーディング標準とベストプラクティス

全プロジェクトに適用可能な普遍的なコーディング標準。

## コード品質の原則

### 1. 可読性を最優先

- コードは書くよりも読まれることが多い
- 明確な変数名と関数名
- 自己文書化されたコードをコメントより優先
- 一貫したフォーマット

### 2. KISS (Keep It Simple, Stupid)

- 動作する最もシンプルなソリューション
- 過度な設計を避ける
- 早すぎる最適化をしない
- 理解しやすいコード > 賢いコード

### 3. DRY (Don't Repeat Yourself)

- 共通ロジックを関数に抽出
- 再利用可能なコンポーネントを作成
- モジュール間でユーティリティを共有
- コピー&ペーストプログラミングを避ける

### 4. YAGNI (You Aren't Gonna Need It)

- 必要になる前に機能を構築しない
- 投機的な汎用化を避ける
- 必要なときだけ複雑さを追加
- シンプルに始めて、必要なときにリファクタリング

## Kotlin 標準

### 変数命名

```kotlin
// ✅ 良い例: 説明的な名前
val marketSearchQuery = "election"
val isUserAuthenticated = true
val totalRevenue = 1000

// ❌ 悪い例: 不明確な名前
val q = "election"
val flag = true
val x = 1000
```

### 関数命名

```kotlin
// ✅ 良い例: 動詞-名詞パターン
suspend fun fetchMarketData(marketId: String): Market
fun calculateSimilarity(a: List<Double>, b: List<Double>): Double
fun isValidEmail(email: String): Boolean

// ❌ 悪い例: 不明確または名詞のみ
suspend fun market(id: String): Market
fun similarity(a: List<Double>, b: List<Double>): Double
fun email(e: String): Boolean
```

### イミュータビリティパターン（CRITICAL）

```kotlin
// ✅ 常に copy() を使用
val updatedUser = user.copy(name = "New Name")

val updatedList = items + newItem
val updatedSet = existingSet + newElement

// ❌ 絶対に直接変更しない
user.name = "New Name"  // 悪い（varの場合）
items.add(newItem)      // 悪い（MutableListの場合）
```

### null安全性（CRITICAL）

```kotlin
// ✅ 良い例: セーフコールとElvis演算子
val name = user?.name ?: "Unknown"
val length = text?.length ?: 0

// ✅ 良い例: letスコープ関数
user?.let { u ->
    println("User: ${u.name}")
} ?: println("User not found")

// ❌ 悪い例: !! 演算子（禁止）
val name = user!!.name  // 絶対禁止
```

### エラーハンドリング

```kotlin
// ✅ 良い例: 包括的なエラーハンドリング
suspend fun fetchData(url: String): Result<Data> = runCatching {
    val response = httpClient.get(url)

    if (!response.status.isSuccess()) {
        throw HttpException("HTTP ${response.status.value}: ${response.status.description}")
    }

    response.body<Data>()
}.onFailure { error ->
    logger.error("Fetch failed", error)
}

// ❌ 悪い例: エラーハンドリングなし
suspend fun fetchData(url: String): Data {
    val response = httpClient.get(url)
    return response.body()
}
```

### Coroutine ベストプラクティス

```kotlin
// ✅ 良い例: 可能な限り並列実行
val (users, markets, stats) = coroutineScope {
    awaitAll(
        async { fetchUsers() },
        async { fetchMarkets() },
        async { fetchStats() }
    )
}

// ❌ 悪い例: 不要な順次実行
val users = fetchUsers()
val markets = fetchMarkets()
val stats = fetchStats()
```

### 型安全性

```kotlin
// ✅ 良い例: 適切な型
data class Market(
    val id: String,
    val name: String,
    val status: MarketStatus,
    val createdAt: Instant
)

enum class MarketStatus {
    ACTIVE, RESOLVED, CLOSED
}

suspend fun getMarket(id: String): Result<Market>

// ❌ 悪い例: Any型の使用
suspend fun getMarket(id: Any): Any
```

## Compose Multiplatform ベストプラクティス

### コンポーネント構造

```kotlin
// ✅ 良い例: 型付き関数型コンポーネント
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    MaterialTheme.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = when (variant) {
                ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
                ButtonVariant.Secondary -> MaterialTheme.colorScheme.secondary
            }
        )
    ) {
        Text(text)
    }
}

enum class ButtonVariant {
    Primary, Secondary
}

// ❌ 悪い例: 型なし、不明確な構造
@Composable
fun Button(props: Map<String, Any>) {
    // 実装
}
```

### 状態管理

```kotlin
// ✅ 良い例: 適切な状態更新
val uiState by viewModel.uiState.collectAsState()

// StateFlowの更新は関数型
_uiState.update { current -> current.copy(isLoading = true) }

// ❌ 悪い例: 直接的な状態参照
val uiState = viewModel.uiState.value  // 再コンポジションされない
```

### 条件付きレンダリング

```kotlin
// ✅ 良い例: 明確な条件付きレンダリング
if (isLoading) {
    CircularProgressIndicator()
}

error?.let {
    ErrorMessage(error = it)
}

data?.let {
    DataDisplay(data = it)
}

// ❌ 悪い例: 三項演算子の地獄
if (isLoading) CircularProgressIndicator() else if (error != null) ErrorMessage(error!!) else if (data != null) DataDisplay(data!!) else null
```

## API 設計標準

### REST API 規約

```
GET    /api/sakes              # 全ての酒をリスト
GET    /api/sakes/:id          # 特定の酒を取得
POST   /api/sakes              # 新しい酒を作成
PUT    /api/sakes/:id          # 酒を更新（全体）
PATCH  /api/sakes/:id          # 酒を更新（部分）
DELETE /api/sakes/:id          # 酒を削除

# フィルタリング用クエリパラメータ
GET /api/sakes?type=junmai&region=niigata&limit=10&offset=0
```

### レスポンスフォーマット

```kotlin
// ✅ 良い例: 一貫したレスポンス構造
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val meta: Meta? = null
)

@Serializable
data class Meta(
    val total: Int,
    val page: Int,
    val limit: Int
)

// 成功レスポンス
ApiResponse(
    success = true,
    data = sakes,
    meta = Meta(total = 100, page = 1, limit = 10)
)

// エラーレスポンス
ApiResponse<Unit>(
    success = false,
    error = "Invalid request"
)
```

### 入力検証

```kotlin
// ✅ 良い例: バリデーション
data class CreateSakeRequest(
    val name: String,
    val type: String,
    val brewery: String,
    val region: String
)

fun CreateSakeRequest.validate(): Result<Unit> = runCatching {
    require(name.isNotBlank() && name.length <= 200) {
        "Name must be 1-200 characters"
    }
    require(type.isNotBlank()) {
        "Type is required"
    }
    require(brewery.isNotBlank() && brewery.length <= 200) {
        "Brewery must be 1-200 characters"
    }
    require(region.isNotBlank()) {
        "Region is required"
    }
}
```

## ファイル構成

### プロジェクト構造

```
sake-hack-kmp/
├── feature/
│   ├── sakelist/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── usecase/
│   │   ├── data/
│   │   │   ├── repository/
│   │   │   ├── source/
│   │   │   └── mapper/
│   │   └── presentation/
│   │       ├── SakeListViewModel.kt
│   │       └── SakeListUiState.kt
├── composeApp/
│   ├── src/
│   │   ├── commonMain/
│   │   ├── androidMain/
│   │   └── iosMain/
└── core/
    ├── network/
    ├── database/
    └── common/
```

### ファイル命名

```
domain/model/Sake.kt              # PascalCase for classes
domain/usecase/GetSakeListUseCase.kt  # PascalCase for classes
presentation/SakeListViewModel.kt     # PascalCase for ViewModels
ui/components/SakeCard.kt             # PascalCase for Composables
```

## コメントとドキュメント

### コメントを書くべき時

```kotlin
// ✅ 良い例: WHYを説明、WHATではない
// 大規模な配列でのパフォーマンスのため、意図的にミューテーションを使用
items.add(newItem)

// APIの過負荷を避けるため、指数バックオフを使用
val delay = min(1000 * 2.0.pow(retryCount).toLong(), 30000)

// ❌ 悪い例: 明白なことを述べる
// カウンターを1増やす
count++

// 名前をユーザーの名前に設定
name = user.name
```

### KDoc for パブリック API

```kotlin
/**
 * セマンティック類似性を使用して酒を検索します。
 *
 * @param query 自然言語の検索クエリ
 * @param limit 最大結果数（デフォルト: 10）
 * @return 類似度スコアでソートされた酒の配列
 * @throws HttpException OpenAI APIが失敗した場合
 *
 * @sample
 * ```kotlin
 * val results = searchSakes("フルーティーな日本酒", 5)
 * println(results.first().name) // "獺祭"
 * ```
 */
suspend fun searchSakes(
    query: String,
    limit: Int = 10
): Result<List<Sake>>
```

## パフォーマンスベストプラクティス

### メモ化

```kotlin
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf

// ✅ 良い例: 高コストな計算をメモ化
val sortedSakes = remember(sakes) {
    sakes.sortedByDescending { it.rating }
}

// ✅ 良い例: 派生状態
val hasHighRatedSakes by remember {
    derivedStateOf {
        sakes.any { it.rating >= 4.5 }
    }
}
```

### 遅延読み込み

```kotlin
// ✅ 良い例: 重いコンポーネントを遅延読み込み
@Composable
fun SakeDetailScreen(sakeId: String) {
    val sake by viewModel.getSake(sakeId).collectAsState(null)

    sake?.let {
        // 酒が読み込まれた後にのみレンダリング
        SakeDetailContent(it)
    } ?: CircularProgressIndicator()
}
```

### データベースクエリ

```kotlin
// ✅ 良い例: 必要なカラムのみ選択
// schema.sq
selectSakeList:
SELECT id, name, type, rating
FROM sake
LIMIT :limit;

// ❌ 悪い例: 全て選択
selectSakeList:
SELECT *
FROM sake;
```

## テスト標準

### テスト構造（AAAパターン）

```kotlin
@Test
fun `calculates similarity correctly`() {
    // Arrange
    val vector1 = listOf(1.0, 0.0, 0.0)
    val vector2 = listOf(0.0, 1.0, 0.0)

    // Act
    val similarity = calculateCosineSimilarity(vector1, vector2)

    // Assert
    assertEquals(0.0, similarity, 0.001)
}
```

### テスト命名

```kotlin
// ✅ 良い例: 説明的なテスト名
@Test
fun `returns empty list when no sakes match query`() { }

@Test
fun `throws exception when API key is missing`() { }

@Test
fun `falls back to local cache when network unavailable`() { }

// ❌ 悪い例: 曖昧なテスト名
@Test
fun `test works`() { }

@Test
fun `test search`() { }
```

## コードスメルの検出

以下のアンチパターンに注意:

### 1. 長い関数

```kotlin
// ❌ 悪い例: 50行以上の関数
fun processSakeData() {
    // 100行のコード
}

// ✅ 良い例: 小さな関数に分割
fun processSakeData(): Result<Sake> {
    val validated = validateData()
    val transformed = transformData(validated)
    return saveData(transformed)
}
```

### 2. 深いネスト

```kotlin
// ❌ 悪い例: 5レベル以上のネスト
if (user != null) {
    if (user.isAdmin) {
        if (sake != null) {
            if (sake.isActive) {
                if (hasPermission) {
                    // 何かする
                }
            }
        }
    }
}

// ✅ 良い例: 早期リターン
user ?: return
if (!user.isAdmin) return
sake ?: return
if (!sake.isActive) return
if (!hasPermission) return

// 何かする
```

### 3. マジックナンバー

```kotlin
// ❌ 悪い例: 説明のない数値
if (retryCount > 3) { }
delay(500)

// ✅ 良い例: 名前付き定数
private const val MAX_RETRIES = 3
private const val DEBOUNCE_DELAY_MS = 500L

if (retryCount > MAX_RETRIES) { }
delay(DEBOUNCE_DELAY_MS)
```

## KMP固有の考慮事項

### expect/actual の最小化

```kotlin
// ✅ 良い例: DIで抽象化
// commonMain
interface DatabaseDriverFactory {
    fun create(): SqlDriver
}

// androidMain
class AndroidDatabaseDriverFactory(
    private val context: Context
) : DatabaseDriverFactory {
    override fun create(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "sake.db")
    }
}

// ❌ 悪い例: 過度なexpect/actual
expect fun createHttpClient(): HttpClient
expect fun savePreference(key: String, value: String)
expect fun loadPreference(key: String): String?
```

**重要**: コード品質は妥協できません。明確で保守可能なコードは、迅速な開発と自信を持ったリファクタリングを可能にします。
