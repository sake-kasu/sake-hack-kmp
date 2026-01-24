---
description: コードの品質、セキュリティ、保守性をレビュー。KMP固有のチェック含む。
---

# Code Review

developブランチとの差分を含む、包括的なセキュリティと品質レビュー：

1. 変更されたファイルを取得:
   - git diff --name-only develop..HEAD (developとの差分)
   - git diff --name-only HEAD (未コミットの変更)

2. 各変更ファイルに対して以下をチェック:

**セキュリティ問題 (CRITICAL):**
- ハードコードされた認証情報、APIキー、トークン
- SQLインジェクション脆弱性
- XSS脆弱性
- 入力検証の欠如
- 安全でない依存関係
- パストラバーサルリスク

**Kotlin安全性 (CRITICAL):**
- `!!` (非nullアサーション) の使用
- 全角括弧 `（）` の使用
- null安全性違反

**Clean Architecture (HIGH):**
- Domain層が他レイヤー(Data/Presentation)に依存
- Repository実装クラスへの直接依存
- ViewModelがUseCaseを経由せずRepositoryを直接使用

**コード品質 (HIGH):**
- 50行以上の関数
- 800行以上のファイル
- 4レベル以上のネストの深さ
- エラーハンドリングの欠如
- println/console.log文
- TODO/FIXMEコメント
- パブリックAPIのKDoc欠如

**MVI パターン (HIGH):**
- 複数のState（単一Stateであるべき）
- var を使用したデータクラス
- MutableListなどのミュータブルコレクション

**デザイン仕様準拠 (HIGH):**
- .penファイルの仕様を無視
- gap, padding, width, heightが仕様と異なる

**ベストプラクティス (MEDIUM):**
- ミューテーションパターン（イミュータブルを使用すべき）
- 絵文字の使用（コード/コメント内）
- 新しいコードのテスト欠如
- アクセシビリティ問題

3. 以下を含むレポートを生成:
   - 重要度: CRITICAL, HIGH, MEDIUM, LOW
   - ファイルの場所と行番号
   - 問題の説明
   - 修正案

4. CRITICALまたはHIGH問題が見つかった場合はコミットをブロック

セキュリティ脆弱性のあるコードは絶対に承認しない！
