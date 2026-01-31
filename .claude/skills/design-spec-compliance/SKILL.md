---
name: design-spec-compliance
description: .penファイルのデザイン仕様に準拠した実装
---

# デザイン仕様準拠スキル

## 最優先事項

**デザイン仕様（.penファイル）は最優先**

このプロジェクトでは、デザイン仕様が技術的な制約よりも優先されます。API仕様の都合でUIを勝手に改変することは禁止されています。

## 使用タイミング

- 新しい画面やコンポーネントを実装する際
- 既存のUIを修正する際
- デザインとAPI仕様の間に不一致がある場合

## デザイン仕様の確認手順

### 1. .penファイルの場所を特定

デザイン仕様は `docs/screens/` ディレクトリに配置されています。

```bash
docs/screens/
├── sake-list.pen          # 酒一覧画面
├── sake-detail.pen        # 酒詳細画面
├── filter-dialog.pen      # フィルターダイアログ
└── ...
```

### 2. 画面番号とコンポーネントIDを確認

.penファイルには以下の情報が含まれています:

- **画面番号**: 画面の識別子
- **コンポーネントID**: 再利用可能なコンポーネントの識別子
- **レイアウト仕様**: gap, padding, width, height など

### 3. レイアウト仕様の確認

特に以下の値を正確に確認してください:

- **gap**: 要素間の間隔
- **padding**: 内側の余白
- **width**: 幅（固定値 or fill_container）
- **height**: 高さ（固定値 or fill_container）
- **font-size**: フォントサイズ
- **color**: 色コード

### 4. デザインシステムの再利用可能コンポーネントを確認

.penファイルには、再利用可能なコンポーネント（ボタン、カード、ダイアログなど）が定義されています。既存のコンポーネントを再利用できる場合は、新規作成せずに既存のものを使用してください。

## 実装例

### 仕様に基づいた実装

```kotlin
// デザイン仕様（.penファイル）:
// - 画面padding: 24dp
// - カード間のgap: 16dp
// - カードの高さ: 120dp
// - カードの幅: fill_container
// - タイトルフォントサイズ: 18sp
// - タイトル色: #1A1A1A

@Composable
fun SakeListScreen(uiState: SakeListUiState) {
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp) // 仕様: gap 16dp
        ) {
            items(uiState.sakes) { sake ->
                SakeCard(
                    sake = sake,
                    modifier = Modifier
                        .fillMaxWidth() // 仕様: fill_container
                        .height(120.dp) // 仕様: 120dp
                )
            }
        }
    }
}
```

## API仕様とデザイン仕様の不一致時の対応

### 基本原則

1. **ユーザーに判断を委ねる** - AskUserQuestionを使用
2. **API都合でUIを勝手に改変しない**
3. **デザイン要求を満たすためのAPI変更を提案**

### 不一致の例

**例1: フィルター項目の不一致**

- デザイン要求: 「酒名・種類・産地」でフィルター
- API仕様: `typeId`、`breweryId` パラメータのみ

**対応**:
```kotlin
// ❌ 悪い例 - API都合でUI要求を無視
@Composable
fun FilterDialog() {
    // typeIdとbreweryIdのみを表示（デザイン仕様を無視）
}

// ✅ 良い例 - ユーザーに確認
// AskUserQuestion を使用して以下を確認:
// 1. デザイン通り「酒名・種類・産地」を実装し、バックエンドAPI変更を依頼するか
// 2. デザインを変更してAPI仕様に合わせるか
// 3. フロントエンドでマッピング処理を追加するか
```

**例2: 表示項目の不一致**

- デザイン要求: 酒の「評価・レビュー数・お気に入り数」を表示
- API仕様: 評価のみ返却、レビュー数・お気に入り数なし

**対応**:
```kotlin
// ❌ 悪い例 - デザインを勝手に変更
@Composable
fun SakeCard(sake: Sake) {
    // 評価のみ表示（デザイン仕様を無視）
    Text("評価: ${sake.rating}")
}

// ✅ 良い例 - ユーザーに確認し、API変更を提案
// AskUserQuestion を使用して:
// 1. バックエンドAPIに「レビュー数」「お気に入り数」の追加を依頼
// 2. 代替案として、別エンドポイントから取得
// 3. デザイン変更の可能性を確認
```

### AskUserQuestion の使用例

```markdown
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
```

## デザインシステムの活用

### 再利用可能なコンポーネント

.penファイルで定義されている再利用可能なコンポーネントを確認し、積極的に活用してください。

```kotlin
// デザインシステムに「PrimaryButton」コンポーネントがある場合

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

### コンポーネントIDの参照

.penファイルのコンポーネントIDを確認し、対応するComposable関数を実装してください。

```kotlin
// .penファイル:
// Component ID: "sake-card-001"
// - width: fill_container
// - height: 120dp
// - padding: 16dp
// - gap: 8dp

@Composable
fun SakeCard(
    sake: Sake,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth() // fill_container
            .height(120.dp) // 仕様: 120dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // 仕様: 16dp
            verticalArrangement = Arrangement.spacedBy(8.dp) // 仕様: gap 8dp
        ) {
            Text(text = sake.name)
            Text(text = sake.type)
        }
    }
}
```

## レスポンシブ対応

デザイン仕様が複数の画面サイズに対応している場合、それぞれの仕様を確認してください。

```kotlin
// モバイル仕様: padding 16dp
// タブレット仕様: padding 24dp

@Composable
fun SakeListScreen() {
    val configuration = LocalConfiguration.current
    val padding = if (configuration.screenWidthDp >= 600) 24.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding) // レスポンシブ対応
    ) {
        // Content
    }
}
```

## デザイン仕様の更新時

デザイン仕様が更新された場合、以下の手順で対応してください:

1. **変更内容を確認**: 新しい.penファイルを確認
2. **影響範囲を特定**: 変更されたコンポーネントを使用している箇所を検索
3. **実装を更新**: 新しい仕様に合わせて実装を修正
4. **プレビューで確認**: Compose Previewまたは実機で視覚的に確認

## チェックリスト

実装時に以下を確認してください:

- [ ] .penファイルを確認したか
- [ ] gap, padding, width, height が仕様通りか
- [ ] font-size, color が仕様通りか
- [ ] デザインシステムの再利用可能コンポーネントを確認したか
- [ ] API仕様との不一致がないか確認したか
- [ ] 不一致がある場合、ユーザーに確認したか
- [ ] デザイン仕様を勝手に変更していないか
