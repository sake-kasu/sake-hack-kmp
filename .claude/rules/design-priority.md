---
name: design-priority
description: デザイン仕様を最優先するルール
severity: error
---

# デザイン優先ルール

このプロジェクトでは、**デザイン仕様（.penファイル）が最優先**です。技術的な制約やAPI仕様の都合でUIを勝手に改変することは禁止されています。

## 最優先事項

### デザイン仕様（.penファイル）が技術仕様より優先

```
優先順位:
1. デザイン仕様（.penファイル）
2. ユーザー要求
3. API仕様
4. 技術的な実装の容易さ
```

**重要**: API仕様がデザイン要求を満たせない場合、デザインを変更するのではなく、API変更を提案してください。

## 必須ルール

### 1. デザイン仕様の確認を最初に実行

新しい画面やコンポーネントを実装する際は、**必ず最初に.penファイルを確認**してください。

```kotlin
// ❌ 悪い例 - デザイン仕様を確認せずに実装
@Composable
fun SakeCard(sake: Sake) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // 適当な値
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // 適当な値
            Text(sake.name)
        }
    }
}

// ✅ 良い例 - .penファイルの仕様に基づいて実装
// デザイン仕様: padding=16dp, gap=8dp, height=120dp
@Composable
fun SakeCard(sake: Sake) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp) // 仕様: 120dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // 仕様: 16dp
            verticalArrangement = Arrangement.spacedBy(8.dp) // 仕様: gap 8dp
        ) {
            Text(sake.name)
        }
    }
}
```

### 2. レイアウト仕様を厳密に守る

以下の値は、.penファイルの仕様を**厳密に**守ってください:

- **gap**: 要素間の間隔
- **padding**: 内側の余白
- **width**: 幅（固定値 or fill_container）
- **height**: 高さ（固定値 or fill_container）
- **font-size**: フォントサイズ
- **color**: 色コード

```kotlin
// デザイン仕様:
// - gap: 16dp
// - padding: 24dp
// - title font-size: 18sp
// - title color: #1A1A1A

@Composable
fun SakeListScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // 仕様: 24dp
        verticalArrangement = Arrangement.spacedBy(16.dp) // 仕様: gap 16dp
    ) {
        Text(
            text = "酒一覧",
            fontSize = 18.sp, // 仕様: 18sp
            color = Color(0xFF1A1A1A) // 仕様: #1A1A1A
        )
    }
}
```

### 3. API仕様との不一致時はユーザーに確認

デザイン仕様とAPI仕様に不一致がある場合、**絶対に**デザインを勝手に変更してはいけません。必ずユーザーに確認してください。

## 禁止事項

### ❌ API都合でUIを勝手に改変

```kotlin
// ❌ 絶対禁止
// デザイン仕様: 「酒名」「種類」「産地」のフィルター
// API仕様: typeId, breweryIdパラメータのみ

@Composable
fun FilterDialog() {
    // APIに合わせてデザインを勝手に変更（禁止）
    Column {
        Text("種類ID")
        OutlinedTextField(value = typeId, onValueChange = {})

        Text("蔵元ID")
        OutlinedTextField(value = breweryId, onValueChange = {})
    }
}
```

### ❌ デザイン仕様を無視した実装

```kotlin
// ❌ 絶対禁止
// デザイン仕様: カードの高さ 120dp

@Composable
fun SakeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // 仕様を無視（禁止）
    ) {
        // ...
    }
}
```

### ❌ 適当な値の使用

```kotlin
// ❌ 絶対禁止
@Composable
fun SakeListScreen() {
    Column(
        modifier = Modifier.padding(16.dp), // 仕様を確認せず適当に設定（禁止）
        verticalArrangement = Arrangement.spacedBy(12.dp) // 適当な値（禁止）
    ) {
        // ...
    }
}
```

## 推奨事項

### ✅ API仕様との不一致時の対応フロー

1. **ユーザーに判断を委ねる** - `AskUserQuestion` を使用
2. **選択肢を提示**:
   - バックエンドAPIの変更を依頼
   - デザイン変更の可能性を確認
   - フロントエンド側での代替案を提示
3. **ユーザーの判断を待つ**

```kotlin
// ✅ 良い例

// AskUserQuestion を使用して確認
/*
デザイン仕様とAPI仕様に不一致があります。

**デザイン要求**:
フィルターダイアログに「酒名」「種類」「産地」のフィルター項目

**API仕様**:
/api/sakes?typeId=xxx&breweryId=xxx （酒名でのフィルターなし）

**選択肢**:
1. バックエンドAPIに `name` パラメータを追加してもらう（デザイン準拠）
2. デザインを変更して「種類」「蔵元」のみのフィルターにする
3. フロントエンドで全件取得後にフィルタリング（パフォーマンス懸念）

どの方針で進めますか?
*/
```

### ✅ デザインシステムの再利用

.penファイルで定義されている再利用可能なコンポーネントを確認し、積極的に活用してください。

```kotlin
// デザインシステムに「PrimaryButton」がある場合

// ❌ 悪い例 - 独自にボタンを作成
@Composable
fun CustomButton() {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE)
        )
    ) {
        Text("ボタン")
    }
}

// ✅ 良い例 - デザインシステムのコンポーネントを使用
@Composable
fun FilterDialog() {
    PrimaryButton(
        text = "適用",
        onClick = { /* action */ }
    )
}
```

## 実例

### 例1: フィルター項目の不一致

**状況**:
- デザイン要求: 「酒名・種類・産地」のフィルター
- API仕様: `typeId`、`breweryId` パラメータのみ

**❌ 間違った対応**:
```kotlin
@Composable
fun FilterDialog() {
    // API仕様に合わせてデザインを変更（禁止）
    Column {
        OutlinedTextField(
            value = typeId,
            onValueChange = {},
            label = { Text("種類ID") }
        )
        OutlinedTextField(
            value = breweryId,
            onValueChange = {},
            label = { Text("蔵元ID") }
        )
    }
}
```

**✅ 正しい対応**:
1. `AskUserQuestion` でユーザーに確認
2. 選択肢を提示:
   - オプションA: バックエンドAPIに `name`, `region` パラメータを追加
   - オプションB: デザイン変更を依頼
   - オプションC: フロントエンド側で全件取得後にフィルタリング
3. ユーザーの判断後に実装

### 例2: 表示項目の不一致

**状況**:
- デザイン要求: 酒の「評価・レビュー数・お気に入り数」を表示
- API仕様: 評価のみ返却、レビュー数・お気に入り数なし

**❌ 間違った対応**:
```kotlin
@Composable
fun SakeCard(sake: Sake) {
    // API仕様に合わせてデザインを無視（禁止）
    Column {
        Text("評価: ${sake.rating}") // 評価のみ表示
        // レビュー数とお気に入り数を勝手に削除
    }
}
```

**✅ 正しい対応**:
1. `AskUserQuestion` でユーザーに確認
2. 選択肢を提示:
   - オプションA: バックエンドAPIに「レビュー数」「お気に入り数」を追加
   - オプションB: 別エンドポイントから取得
   - オプションC: デザイン変更を依頼
3. ユーザーの判断後に実装

### 例3: レイアウト値の変更

**状況**:
- デザイン仕様: カードの高さ 120dp
- 開発者判断: 100dpの方が見栄えが良い

**❌ 間違った対応**:
```kotlin
@Composable
fun SakeCard() {
    Card(
        modifier = Modifier.height(100.dp) // 勝手に変更（禁止）
    ) {
        // ...
    }
}
```

**✅ 正しい対応**:
```kotlin
@Composable
fun SakeCard() {
    Card(
        modifier = Modifier.height(120.dp) // 仕様通り
    ) {
        // ...
    }
}

// もし変更が必要だと思う場合は、ユーザー（デザイナー）に相談
```

## チェックリスト

実装前に以下を確認してください:

- [ ] .penファイルを確認したか
- [ ] gap, padding, width, height が仕様通りか
- [ ] font-size, color が仕様通りか
- [ ] デザインシステムの再利用可能コンポーネントを確認したか
- [ ] API仕様との不一致がないか
- [ ] 不一致がある場合、ユーザーに確認したか
- [ ] デザイン仕様を勝手に変更していないか

## まとめ

- デザイン仕様（.penファイル）が**最優先**
- API仕様との不一致時は**必ずユーザーに確認**
- デザインを勝手に変更することは**絶対禁止**
- レイアウト値（gap, padding, width, height）は**厳密に守る**
- デザインシステムの再利用可能コンポーネントを**積極的に活用**
