---
description: 要求を再確認し、リスクを評価し、ステップバイステップの実装計画を作成。コードに触れる前にユーザーの確認を待つ。
---

# Plan コマンド

このコマンドは、コードを書く前に包括的な実装計画を作成する **planner** エージェントを起動します。

## このコマンドの機能

1. **要求の再確認** - 何を構築する必要があるかを明確化
2. **リスクの特定** - 潜在的な問題とブロッカーを洗い出し
3. **ステッププランの作成** - 実装をフェーズに分解
4. **確認を待つ** - 進める前にユーザーの承認を必ず受ける

## 使用タイミング

以下の場合に `/plan` を使用してください:
- 新しい機能を開始するとき
- 重要なアーキテクチャ変更を行うとき
- 複雑なリファクタリングに取り組むとき
- 複数のファイル/コンポーネントが影響を受けるとき
- 要求が不明確または曖昧なとき

## 動作方法

planner エージェントは以下を実行します:

1. **リクエストを分析** し、明確な用語で要求を再記述
2. **フェーズに分解** し、具体的で実行可能なステップを作成
3. **依存関係を特定** コンポーネント間の関係を明示
4. **リスクを評価** 潜在的なブロッカーを特定
5. **複雑度を推定** (高/中/低)
6. **計画を提示** し、明示的な確認を待つ

## 使用例

```
User: /plan フィルターダイアログを実装したい

Agent (planner):
# 実装計画: フィルターダイアログ

## 要求の再確認
- 酒一覧画面にフィルターダイアログを追加
- 「種類」「産地」「評価」でフィルタリング可能
- フィルター条件はローカルストレージに保存
- デザイン仕様（.pen）に準拠

## 実装フェーズ

### Phase 1: Domain層
1. FilterCriteria モデルを作成（domain/model/FilterCriteria.kt）
   - アクション: data class FilterCriteria を定義
   - 理由: フィルター条件を型安全に管理
   - 依存関係: なし
   - リスク: 低

2. FilterSakesUseCase を作成（domain/usecase/FilterSakesUseCase.kt）
   - アクション: フィルターロジックを実装
   - 理由: ビジネスロジックをDomain層に配置
   - 依存関係: FilterCriteria
   - リスク: 低

### Phase 2: Presentation層
3. SakeListUiState を更新（presentation/SakeListUiState.kt）
   - アクション: filterCriteria プロパティを追加
   - 理由: フィルター状態をUIStateで管理
   - 依存関係: FilterCriteria
   - リスク: 低

4. SakeListViewModel に Intent を追加（presentation/SakeListViewModel.kt）
   - アクション: applyFilter(criteria: FilterCriteria) 関数を追加
   - 理由: フィルター適用のIntentを定義
   - 依存関係: FilterSakesUseCase
   - リスク: 低

### Phase 3: UI層
5. FilterDialog Composable を作成（ui/FilterDialog.kt）
   - アクション: Material 3 AlertDialog でフィルターUIを作成
   - 理由: ユーザーがフィルター条件を選択
   - 依存関係: デザイン仕様（.pen）
   - リスク: 中（デザイン仕様との整合性）

6. SakeListScreen にフィルターボタンを追加
   - アクション: FloatingActionButton でダイアログを表示
   - 理由: ユーザーがフィルターダイアログにアクセス
   - 依存関係: FilterDialog
   - リスク: 低

### Phase 4: データ永続化
7. LocalStorage でフィルター条件を保存（予定）
   - アクション: DataStore を使用してフィルター条件を保存
   - 理由: アプリ再起動後もフィルター条件を復元
   - 依存関係: Phase 2 完了
   - リスク: 低

## デザイン仕様準拠

.penファイル（docs/screens/filter-dialog.pen）を確認:
- padding: 24dp
- gap: 16dp
- ボタンの高さ: 48dp
- 入力フィールドの高さ: 56dp

## リスク
- 中: API仕様がフィルターパラメータに対応していない可能性
  - 緩和策: API仕様を確認し、必要ならバックエンド変更を依頼
- 低: フィルター条件の永続化パフォーマンス

## 推定複雑度: 低〜中
- Domain層: 1時間
- Presentation層: 1時間
- UI層: 2〜3時間（デザイン準拠）
- データ永続化: 1時間
- 合計: 5〜6時間

**確認待ち**: この計画で進めますか？（yes/no/modify）
```

## 重要な注意事項

**CRITICAL**: planner エージェントは、「yes」や「proceed」などの明示的な確認応答を受け取るまで、**コードを一切書きません**。

変更が必要な場合は、以下のように応答してください:
- "modify: [変更内容]"
- "別のアプローチ: [代替案]"
- "Phase 2をスキップしてPhase 3から始める"

## KMP固有の考慮事項

計画時に以下を確認してください:

### Clean Architecture の実装順序
1. Domain層 → Presentation層 → Data層 → UI層
2. 各レイヤーの依存関係が正しいか

### デザイン仕様の確認
- `.pen`ファイルを最初に確認
- gap, padding, width, height を仕様通りに計画

### API仕様との整合性
- API仕様がデザイン要求を満たせるか確認
- 不一致がある場合、ユーザーに確認

## 他のコマンドとの連携

計画後:
- `/code-review` を使用して実装をレビュー
- ビルドエラーが発生した場合は修正

## 関連エージェント

このコマンドは以下のエージェントを起動します:
`.claude/agents/planner.md`
