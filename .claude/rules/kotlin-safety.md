---
name: kotlin-safety
description: Kotlinコードの安全性を保証するルール
severity: error
---

# Kotlin 安全性ルール

このルールは、Kotlinコードの安全性を保証するために**絶対に守らなければならない**制約を定義します。

## 絶対禁止事項

### 1. `!!` (非nullアサーション) の使用禁止

**理由**: `!!` 演算子は、値がnullの場合にNullPointerException (NPE) を発生させます。これは実行時エラーであり、アプリのクラッシュにつながります。

```kotlin
// ❌ 絶対禁止
val user = getUserOrNull()!!
val name = user.name

// ✅ 正しい方法
val name = getUserOrNull()?.name ?: "Unknown"
```

**例外**: `!!` の使用は**いかなる場合も認められません**。以下の代替手段を使用してください:

- `?.` (セーフコール)
- `?:` (Elvis演算子)
- `let`、`also`、`run` などのスコープ関数
- `requireNotNull()` または `checkNotNull()` (明示的な契約違反を示す場合のみ)

### 2. 全角括弧 `（）` の使用禁止

**理由**: Kotlinのコードでは半角括弧 `()` のみが有効です。全角括弧はコンパイルエラーまたは予期しない動作の原因になります。

```kotlin
// ❌ 絶対禁止
fun getSakeName（）: String { // 全角括弧
    return "獺祭"
}

// ✅ 正しい方法
fun getSakeName(): String { // 半角括弧
    return "獺祭"
}
```

**適用範囲**: コード内の全ての括弧（関数定義、関数呼び出し、条件式、ラムダ式など）

## 必須事項

### 1. null安全性の徹底

null許容型を扱う際は、以下のいずれかの方法を使用してください:

#### セーフコール (`?.`)

```kotlin
// ✅ 良い例
val length = text?.length
val userName = user?.profile?.name
```

#### Elvis演算子 (`?:`)

```kotlin
// ✅ 良い例
val name = getUserOrNull()?.name ?: "Unknown"
val count = getCountOrNull() ?: 0
```

#### スコープ関数 (`let`, `also`, `run`)

```kotlin
// ✅ 良い例 - let
getUserOrNull()?.let { user ->
    println("User: ${user.name}")
    saveUser(user)
} ?: println("User not found")

// ✅ 良い例 - also
val user = getUserOrNull()?.also {
    println("User found: ${it.name}")
} ?: return

// ✅ 良い例 - run
val result = getUserOrNull()?.run {
    "${name} (${email})"
} ?: "No user"
```

#### requireNotNull / checkNotNull (契約違反時のみ)

```kotlin
// ✅ 良い例 - 明示的な契約違反を示す
fun processUser(userId: String) {
    val user = repository.getUser(userId)
    requireNotNull(user) { "User with ID $userId must exist" }
    // userは確実に非nullとして扱える
}
```

### 2. イミュータビリティの徹底

#### データクラスは全て `val`

```kotlin
// ✅ 良い例
data class Sake(
    val id: String,
    val name: String,
    val type: String
)

// ❌ 悪い例
data class Sake(
    var id: String,
    var name: String,
    var type: String
)
```

#### イミュータブルコレクション

```kotlin
// ✅ 良い例
val sakes: List<Sake> = listOf(...)
val types: Set<String> = setOf("純米大吟醸", "純米吟醸")
val sakeMap: Map<String, Sake> = mapOf(...)

// ❌ 悪い例
val sakes: MutableList<Sake> = mutableListOf(...)
val types: MutableSet<String> = mutableSetOf()
```

#### State の更新は `copy()`

```kotlin
// ✅ 良い例
data class UiState(
    val sakes: List<Sake> = emptyList(),
    val isLoading: Boolean = false
)

_uiState.update { it.copy(sakes = newSakes, isLoading = false) }

// ❌ 悪い例
data class UiState(
    var sakes: List<Sake> = emptyList(),
    var isLoading: Boolean = false
)

_uiState.value.sakes = newSakes
_uiState.value.isLoading = false
```

## 違反時の対応

### コードレビュー時

- `!!` 演算子を発見した場合: **即座に修正を要求**
- 全角括弧を発見した場合: **即座に修正を要求**
- `var` の使用を発見した場合: **必要性を確認し、可能な限り `val` に変更**

### 自動チェック

以下のフックを使用して、自動的にチェックを実行してください:

```json
{
  "PostToolUse": [
    {
      "matcher": "tool == \"Edit\" && tool_input.file_path matches \"\\.kt$\"",
      "hooks": [
        {
          "type": "command",
          "command": "node .claude/scripts/check-kotlin-safety.js $file_path"
        }
      ]
    }
  ]
}
```

## 例外ケース

### requireNotNull / checkNotNull の使用

以下の条件を**全て**満たす場合のみ、`requireNotNull()` または `checkNotNull()` の使用が認められます:

1. **契約違反が明確**: 値がnullになることがプログラムの論理的な誤りである
2. **早期失敗が望ましい**: nullの場合、即座にエラーとして扱うべき
3. **エラーメッセージを提供**: `requireNotNull(value) { "明確なエラーメッセージ" }`

```kotlin
// ✅ 許容される例
fun loadUserProfile(userId: String) {
    val user = repository.getUser(userId)
    requireNotNull(user) { "User $userId must exist in the database" }
    // この後、userは非nullとして扱える
}

// ❌ 許容されない例
val name = user?.name
requireNotNull(name) // エラーメッセージなし、セーフコールで対応すべき
```

### Platformコード内でのnull扱い

Androidフレームワークなど、プラットフォーム固有のコードで、nullチェックが保証されている場合:

```kotlin
// Android固有コード
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // savedInstanceStateは最初の起動時はnullだが、フレームワークが保証
}
```

この場合でも、`?.` や `let` を使用してnull安全に扱うことが推奨されます。

## まとめ

- `!!` の使用は**絶対禁止**
- 全角括弧 `（）` の使用は**絶対禁止**
- null安全性は `?.`, `?:`, `let` で対応
- データクラスは `val` のみ
- コレクションはイミュータブル
- State更新は `copy()` を使用
