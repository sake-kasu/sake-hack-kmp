---
description: Kotlinコンパイルエラーとビルドエラーを段階的に修正。
---

# Build and Fix

Kotlinコンパイルエラーとビルドエラーを段階的に修正:

1. ビルド実行: ./gradlew build または Android Studioでビルド

2. エラー出力を解析:
   - ファイルごとにグループ化
   - 重要度でソート

3. 各エラーに対して:
   - エラーコンテキストを表示（前後5行）
   - 問題を説明
   - 修正案を提案
   - 修正を適用
   - ビルドを再実行
   - エラーが解決されたか検証

4. 以下の場合は停止:
   - 修正が新しいエラーを導入
   - 同じエラーが3回の試行後も継続
   - ユーザーが一時停止を要求

5. サマリーを表示:
   - 修正したエラー数
   - 残りのエラー数
   - 導入された新しいエラー数

## Kotlin/KMP固有のエラーパターン

**型推論失敗:**
- emptyList()の型を明示: `emptyList<Sake>()`

**null安全性違反:**
- `!!` の代わりに `?.` と `?:` を使用
- let/also/runスコープ関数を活用

**expect/actual不一致:**
- シグネチャを完全に一致させる
- 戻り値の型、パラメータ数、名前を確認

**SQLDelightエラー:**
- スキーマとクエリのカラム名を確認
- generateCommonMainDatabaseInterface を実行

**Koin DIエラー:**
- モジュール定義を確認
- single/factory/viewModelが正しく定義されているか

**Compose エラー:**
- @Composableアノテーションの欠如
- Composable関数内でのHook呼び出し

安全のため一度に1つのエラーを修正！
