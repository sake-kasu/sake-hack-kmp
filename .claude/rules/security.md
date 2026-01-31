---
name: security
description: セキュリティガイドライン
severity: error
---

# セキュリティガイドライン

## 必須のセキュリティチェック

**全てのコミット前に以下を確認**:
- [ ] ハードコードされたシークレットがない（APIキー、パスワード、トークン）
- [ ] 全てのユーザー入力が検証されている
- [ ] SQLインジェクション対策（パラメータ化クエリ）
- [ ] XSS対策（HTMLサニタイズ）
- [ ] CSRF保護が有効
- [ ] 認証/認可が検証されている
- [ ] 全てのエンドポイントにレート制限
- [ ] エラーメッセージが機密データを漏洩しない

## シークレット管理

### ❌ 絶対禁止: ハードコードされたシークレット

```kotlin
// ❌ 絶対禁止
const val API_KEY = "sk-proj-xxxxx"
const val DATABASE_PASSWORD = "mypassword123"
```

### ✅ 必須: 環境変数

```kotlin
// ✅ 良い例 - 環境変数から取得
val apiKey = System.getenv("OPENAI_API_KEY")
    ?: throw IllegalStateException("OPENAI_API_KEY not configured")

val dbPassword = System.getenv("DATABASE_PASSWORD")
    ?: throw IllegalStateException("DATABASE_PASSWORD not configured")
```

### KMP での環境変数の扱い

```kotlin
// commonMain - expect 宣言
expect object EnvConfig {
    val apiKey: String
    val baseUrl: String
}

// androidMain - actual 実装
actual object EnvConfig {
    actual val apiKey: String = BuildConfig.API_KEY
    actual val baseUrl: String = BuildConfig.BASE_URL
}

// iosMain - actual 実装
actual object EnvConfig {
    actual val apiKey: String = Platform.getEnv("API_KEY")
        ?: error("API_KEY not configured")
    actual val baseUrl: String = Platform.getEnv("BASE_URL")
        ?: error("BASE_URL not configured")
}
```

## 入力検証

### ユーザー入力の検証

```kotlin
// ❌ 悪い例 - 検証なし
fun searchSakes(query: String): List<Sake> {
    return repository.search(query) // SQLインジェクションリスク
}

// ✅ 良い例 - 入力検証
fun searchSakes(query: String): Result<List<Sake>> {
    // 入力検証
    if (query.isBlank()) {
        return Result.failure(IllegalArgumentException("Query cannot be blank"))
    }
    if (query.length > 100) {
        return Result.failure(IllegalArgumentException("Query too long"))
    }
    // SQLインジェクション対策（パラメータ化クエリ）
    return runCatching {
        repository.search(query.sanitize())
    }
}

private fun String.sanitize(): String {
    // 特殊文字のエスケープ
    return this.replace(Regex("[^a-zA-Z0-9\\s]"), "")
}
```

## SQLインジェクション対策

### SQLDelight を使用（推奨）

```kotlin
// ✅ 良い例 - SQLDelight（パラメータ化クエリ）
// schema.sq
searchSakes:
SELECT * FROM sake
WHERE name LIKE '%' || :query || '%';

// Kotlin
fun searchSakes(query: String): List<Sake> {
    return database.sakeQueries.searchSakes(query).executeAsList()
}
```

### ❌ 生SQLの文字列連結は禁止

```kotlin
// ❌ 絶対禁止 - SQLインジェクションリスク
val query = "SELECT * FROM sake WHERE name = '$userInput'"
```

## XSS対策

Compose Multiplatformでは、デフォルトでXSSのリスクは低いですが、WebViewを使用する場合は注意が必要です。

```kotlin
// WebView使用時
@Composable
fun WebViewScreen(htmlContent: String) {
    // ❌ 悪い例 - サニタイズなし
    WebView(htmlContent)

    // ✅ 良い例 - HTMLサニタイズ
    val sanitizedHtml = htmlContent.sanitize()
    WebView(sanitizedHtml)
}

private fun String.sanitize(): String {
    return this
        .replace("<script>", "&lt;script&gt;")
        .replace("</script>", "&lt;/script&gt;")
        .replace("javascript:", "")
}
```

## 認証と認可

### トークンの安全な管理

```kotlin
// ✅ 良い例 - トークンを安全に保存
class TokenManager(private val secureStorage: SecureStorage) {
    suspend fun saveToken(token: String) {
        // 暗号化してストレージに保存
        secureStorage.encryptAndSave("auth_token", token)
    }

    suspend fun getToken(): String? {
        return secureStorage.decryptAndGet("auth_token")
    }

    suspend fun clearToken() {
        secureStorage.remove("auth_token")
    }
}

// ❌ 悪い例 - プレーンテキストで保存
class TokenManager {
    fun saveToken(token: String) {
        // プレーンテキストで保存（危険）
        preferences.putString("auth_token", token)
    }
}
```

### API リクエストの認証

```kotlin
// ✅ 良い例 - 全てのAPIリクエストに認証ヘッダー
class AuthenticatedHttpClient(
    private val tokenManager: TokenManager
) {
    private val client = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenManager.getToken()
                    BearerTokens(token ?: "", "")
                }
            }
        }
    }
}
```

## レート制限

API呼び出しにレート制限を実装してください（主にバックエンド側で実装）。

```kotlin
// フロントエンド側での簡易的なレート制限
class RateLimitedRepository(
    private val repository: SakeRepository
) : SakeRepository {
    private val rateLimiter = RateLimiter(maxRequests = 10, perSeconds = 60)

    override suspend fun getSakes(): Result<List<Sake>> {
        if (!rateLimiter.tryAcquire()) {
            return Result.failure(TooManyRequestsException("Rate limit exceeded"))
        }
        return repository.getSakes()
    }
}
```

## エラーメッセージ

### ❌ 機密情報を含むエラーメッセージ

```kotlin
// ❌ 悪い例
catch (e: Exception) {
    _uiState.update {
        it.copy(error = "Database error: ${e.message}") // 内部詳細を露出
    }
}
```

### ✅ ユーザー向けの安全なエラーメッセージ

```kotlin
// ✅ 良い例
catch (e: Exception) {
    // ログには詳細を記録
    logger.error("Database error", e)

    // ユーザーには一般的なメッセージを表示
    _uiState.update {
        it.copy(error = "データの取得に失敗しました。後でもう一度お試しください。")
    }
}
```

## セキュリティ問題発見時の対応プロトコル

セキュリティ問題を発見した場合:

1. **即座に停止** - コードの変更を中断
2. **security-reviewer エージェントを使用** - 専門的なレビューを実施
3. **CRITICAL問題を修正** - 続行前に必ず修正
4. **露出したシークレットをローテーション** - APIキーなどを再発行
5. **コードベース全体をレビュー** - 類似の問題がないか確認

## セキュリティチェックリスト

コミット前に以下を確認:

### Secrets（CRITICAL）
- [ ] APIキー、パスワード、トークンがハードコードされていない
- [ ] 環境変数から取得している
- [ ] `.env` ファイルが `.gitignore` に含まれている

### 入力検証（HIGH）
- [ ] 全てのユーザー入力が検証されている
- [ ] 入力長の制限が実装されている
- [ ] 特殊文字のエスケープが実装されている

### SQL/データベース（HIGH）
- [ ] パラメータ化クエリを使用（SQLDelight推奨）
- [ ] 生SQLの文字列連結を使用していない

### 認証/認可（HIGH）
- [ ] トークンが安全に保存されている（暗号化）
- [ ] 全てのAPIリクエストに認証ヘッダー
- [ ] トークンの有効期限チェック

### エラーハンドリング（MEDIUM）
- [ ] エラーメッセージが機密情報を含まない
- [ ] 内部エラーはログに記録、ユーザーには一般的なメッセージ

### 依存関係（MEDIUM）
- [ ] 依存関係が最新で脆弱性がない
- [ ] 定期的に依存関係の更新を実施

## まとめ

- ハードコードされたシークレットは**絶対禁止**
- 全てのユーザー入力を**検証**
- パラメータ化クエリで**SQLインジェクション対策**
- トークンは**暗号化して保存**
- エラーメッセージで**機密情報を露出しない**
- セキュリティ問題発見時は**即座に停止して修正**
