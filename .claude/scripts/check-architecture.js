#!/usr/bin/env node

/**
 * アーキテクチャチェックスクリプト
 *
 * Domain層のファイルが他レイヤー（Data, Presentation）に依存していないかをチェックします。
 */

const fs = require('fs');
const path = require('path');

// コマンドライン引数からファイルパスを取得
const filePath = process.argv[2];

if (!filePath) {
  console.error('エラー: ファイルパスが指定されていません');
  process.exit(1);
}

// ファイルが存在するか確認
if (!fs.existsSync(filePath)) {
  console.error(`エラー: ファイルが見つかりません: ${filePath}`);
  process.exit(1);
}

// Domain層のファイルかどうかを確認
if (!filePath.includes('/domain/')) {
  // Domain層以外のファイルはチェックしない
  console.log(`ℹ️  Domain層以外のファイル: スキップ (${filePath})`);
  process.exit(0);
}

// ファイルを読み込み
const content = fs.readFileSync(filePath, 'utf-8');
const lines = content.split('\n');

let hasErrors = false;
const errors = [];

// 禁止されたインポートパターン
const forbiddenImports = [
  { pattern: /import\s+.*\.data\./, layer: 'Data' },
  { pattern: /import\s+.*\.presentation\./, layer: 'Presentation' },
  { pattern: /import\s+.*ViewModel/, layer: 'Presentation (ViewModel)' },
  { pattern: /import\s+.*UiState/, layer: 'Presentation (UiState)' },
  { pattern: /import\s+.*\.repository\..*Impl/, layer: 'Data (Repository実装)' },
];

// 各行をチェック
lines.forEach((line, index) => {
  const lineNumber = index + 1;

  forbiddenImports.forEach(({ pattern, layer }) => {
    if (pattern.test(line)) {
      errors.push({
        line: lineNumber,
        importedLayer: layer,
        statement: line.trim()
      });
      hasErrors = true;
    }
  });
});

// 結果を出力
if (hasErrors) {
  console.error('\n❌ アーキテクチャチェック: エラーが見つかりました\n');
  console.error(`ファイル: ${filePath}\n`);
  console.error('Domain層が他レイヤーに依存しています:\n');

  errors.forEach(error => {
    console.error(`行 ${error.line}: ${error.importedLayer}層への依存`);
    console.error(`  ${error.statement}`);
  });

  console.error('\n💡 修正方法:');
  console.error('  - Domain層はインターフェースのみに依存してください');
  console.error('  - Repository実装クラス（*Impl）ではなく、Repositoryインターフェースを使用してください');
  console.error('  - ViewModelやUiStateへの依存は禁止されています\n');
  console.error('詳細は .claude/rules/architecture.md を参照してください。\n');

  process.exit(1);
} else {
  console.log(`✅ アーキテクチャチェック: 問題なし (${filePath})`);
  process.exit(0);
}
