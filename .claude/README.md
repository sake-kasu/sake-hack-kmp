# Claude Code 設定 - sake-hack-kmp

このディレクトリには、sake-hack-kmp プロジェクト用の Claude Code 設定が含まれています。

## 概要

`.claude/` ディレクトリは、Claude Code が効率的に開発を支援するための設定ファイル群を格納しています。Kotlin Multiplatform (KMP) 開発に特化したスキル、ルール、コマンド、エージェントが定義されています。

## ディレクトリ構成

```
.claude/
├── README.md           # このファイル
├── agents/             # 専門エージェント定義
├── skills/             # 再利用可能な知識ベース・手順書
│   ├── kotlin-multiplatform/    # KMP開発ベストプラクティス
│   ├── compose-multiplatform/   # Compose UI開発パターン
│   └── design-spec-compliance/  # デザイン仕様準拠ルール
├── commands/           # `/コマンド名` で呼び出せるショートカット
├── rules/              # プロジェクト全体で必ず守るルール
│   ├── kotlin-safety.md         # Kotlin安全性ルール
│   ├── architecture.md          # Clean Architecture + MVI
│   └── design-priority.md       # デザイン仕様優先ルール
├── hooks/              # ツール実行時の自動化設定
├── contexts/           # コンテキスト定義
└── scripts/            # Node.js スクリプト
```

## 主要な構成要素

### Skills（スキル）

再利用可能な知識ベースと手順書。Claude が自動的に参照します。

#### KMP固有スキル

- **kotlin-multiplatform**: KMP開発のベストプラクティス
  - null安全性の徹底（`!!` 禁止、`?.`, `?:`, `let` の使用）
  - プラットフォーム分離の原則（expect/actual は最小限）
  - Clean Architecture の遵守
  - Coroutine と Flow の使用
  - データクラスとイミュータビリティ

- **compose-multiplatform**: Compose UI開発パターン
  - コンポーネント構成の原則
  - 状態管理（StateFlow, collectAsState）
  - レイアウト（gap, padding, Modifier順序）
  - デザイン仕様準拠

- **design-spec-compliance**: デザイン仕様準拠
  - .penファイルの確認手順
  - API仕様との不一致時の対応
  - デザインシステムの活用

### Rules（ルール）

プロジェクト全体で必ず守るべきルール。常に適用されます。

- **kotlin-safety.md**: Kotlin安全性ルール
  - `!!` 演算子の使用を完全禁止
  - 全角括弧 `（）` の使用を完全禁止
  - null安全性の徹底
  - イミュータビリティの徹底

- **architecture.md**: Clean Architecture + MVI パターン
  - レイヤー構造の遵守
  - Domain層は他レイヤーに非依存
  - Repository はインターフェース/実装分離
  - ViewModel は UseCase 経由でアクセス
  - 実装順序（Domain → Presentation → Data → UI）

- **design-priority.md**: デザイン優先ルール
  - デザイン仕様（.penファイル）が最優先
  - API都合でUIを勝手に改変禁止
  - API仕様との不一致時はユーザーに確認

### Agents（エージェント）

特定のタスクを専門的に処理するエージェント。

- **planner**: 実装計画の作成
- **code-reviewer**: コード品質レビュー
- **build-error-resolver**: ビルドエラー解決

### Commands（コマンド）

`/コマンド名` で呼び出せるショートカット。

- `/plan` - 実装計画の作成
- `/code-review` - コード品質レビュー
- `/build-fix` - ビルドエラー解決

### Hooks（フック）

ツール実行時に自動的に実行される処理。

- Kotlinファイル編集後の安全性チェック（`!!` 演算子、全角括弧の検出）
- Domain層ファイル作成時の依存関係チェック

## 使い方

### スキルの使用

Claude が自動的に参照しますが、明示的に指定することも可能です:

```
User: kotlin-multiplatform スキルに従ってFilterCriteriaを実装して
```

### コマンドの使用

```
User: /plan フィルターダイアログを実装したい
```

コマンドを実行すると、対応するエージェントが起動し、KMPスキルを参照して作業を進めます。

### ルールの適用

`rules/` 配下のルールは常に適用されます。Claude は以下のルールを自動的に遵守します:

- Kotlin安全性ルール（`!!` 禁止、全角括弧禁止）
- Clean Architecture + MVI パターン
- デザイン仕様優先

## 主要なルール

### Kotlin安全性

```kotlin
// ❌ 禁止
val name = user!!.name
fun getName（）: String // 全角括弧

// ✅ 推奨
val name = user?.name ?: "Unknown"
fun getName(): String // 半角括弧
```

### Clean Architecture

```kotlin
// ✅ 良い例 - Domain層はインターフェースのみに依存
class GetSakeListUseCase(
    private val repository: SakeRepository // インターフェース
) {
    suspend operator fun invoke() = repository.getSakes()
}

// ❌ 悪い例 - Domain層が実装クラスに依存
class GetSakeListUseCase(
    private val repository: SakeRepositoryImpl // 実装クラス（NG）
)
```

### デザイン優先

```kotlin
// デザイン仕様: padding=24dp, gap=16dp, height=120dp

@Composable
fun SakeListScreen() {
    Column(
        modifier = Modifier.padding(24.dp), // 仕様通り
        verticalArrangement = Arrangement.spacedBy(16.dp) // 仕様通り
    ) {
        SakeCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp) // 仕様通り
        )
    }
}
```

## カスタマイズ

プロジェクト固有の設定を追加する方法:

### 新しいスキルを追加

```bash
mkdir -p .claude/skills/my-custom-skill
```

`SKILL.md` を作成:

```markdown
---
name: my-custom-skill
description: カスタムスキルの説明
---

# カスタムスキル

## 使用タイミング
...

## ルール
...
```

### 新しいルールを追加

`.claude/rules/` に新しいマークダウンファイルを作成:

```markdown
---
name: my-custom-rule
description: カスタムルールの説明
severity: error
---

# カスタムルール

...
```

### 新しいコマンドを追加

`.claude/commands/` に新しいマークダウンファイルを作成:

```markdown
---
description: カスタムコマンドの説明
---

# /my-command コマンド

## 実行内容
...
```

### フックを追加

`.claude/hooks/hooks.json` を編集:

```json
{
  "PostToolUse": [
    {
      "matcher": "tool == \"Edit\" && tool_input.file_path matches \"\\.kt$\"",
      "hooks": [
        {
          "type": "command",
          "command": "node .claude/scripts/my-custom-script.js $file_path"
        }
      ],
      "description": "カスタムフックの説明"
    }
  ]
}
```

## CLAUDE.md との関係

### 役割分担

- **CLAUDE.md**: プロジェクト固有のガイドライン
  - コミットメッセージフォーマット
  - 開発コマンド
  - プロジェクト構造
  - プロジェクト固有のルール

- **.claude/**: 汎用的な知識ベース・ツール
  - KMP開発ベストプラクティス
  - Clean Architecture パターン
  - 再利用可能なスキル・ルール・コマンド

### 併用

両方を併用することで、プロジェクト固有の要求と汎用的なベストプラクティスを組み合わせて効率的に開発できます。

## よくある質問

### Q: スキルを無効化できますか?

A: はい。スキルディレクトリを別の場所に移動するか、ファイル名を変更（例: `SKILL.md` → `SKILL.md.disabled`）してください。

### Q: ルールの優先順位は?

A: 全てのルールが同等に適用されます。severity が `error` のルールは必ず守る必要があります。

### Q: カスタムコマンドを作成できますか?

A: はい。`.claude/commands/` に新しいマークダウンファイルを追加してください。

### Q: フックが実行されない場合は?

A: `.claude/hooks/hooks.json` の `matcher` 条件を確認してください。また、スクリプトファイルが存在し、実行可能であることを確認してください。

## トラブルシューティング

### スキルが参照されない

1. スキルファイルが正しい場所（`.claude/skills/[skill-name]/SKILL.md`）にあるか確認
2. フロントマター（`---` で囲まれた部分）が正しく記述されているか確認
3. Claude に「[skill-name] スキルを参照して」と明示的に指示

### ルールが適用されない

1. ルールファイルが `.claude/rules/` にあるか確認
2. フロントマターが正しく記述されているか確認
3. Claude が最新のプロジェクト状態を認識しているか確認

### コマンドが見つからない

1. コマンドファイルが `.claude/commands/` にあるか確認
2. ファイル名とコマンド名が一致しているか確認（例: `plan.md` → `/plan`）
3. フロントマターに `description` が記述されているか確認

## 参考資料

- [Everything Claude Code](https://github.com/anthropics/everything-claude-code) - オリジナルのリポジトリ
- [CLAUDE.md](../CLAUDE.md) - プロジェクト固有のガイドライン
- [architecture.md](../architecture.md) - 詳細なアーキテクチャガイド

## 貢献

このプロジェクトに貢献したい場合:

1. 新しいスキル・ルール・コマンドを追加
2. 既存のファイルを改善
3. ドキュメントを更新

変更を加えた際は、このREADME.mdも更新してください。

## ライセンス

このプロジェクトは、sake-hack-kmp プロジェクトのライセンスに従います。
