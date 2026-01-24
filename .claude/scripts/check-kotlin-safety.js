#!/usr/bin/env node

/**
 * Kotlin 安全性チェックスクリプト
 *
 * 以下の項目をチェックします:
 * 1. !! (非nullアサーション) の使用
 * 2. 全角括弧 （） の使用
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

// ファイルを読み込み
const content = fs.readFileSync(filePath, 'utf-8');
const lines = content.split('\n');

let hasErrors = false;
const errors = [];

// 各行をチェック
lines.forEach((line, index) => {
  const lineNumber = index + 1;

  // !! 演算子のチェック（コメント内を除外）
  const codeWithoutComments = line.split('//')[0];
  if (codeWithoutComments.includes('!!')) {
    errors.push({
      line: lineNumber,
      type: '!! 演算子',
      message: '!! (非nullアサーション) の使用は禁止されています',
      suggestion: '?.（セーフコール）、?:（Elvis演算子）、またはletスコープ関数を使用してください'
    });
    hasErrors = true;
  }

  // 全角括弧のチェック
  if (line.includes('（') || line.includes('）')) {
    errors.push({
      line: lineNumber,
      type: '全角括弧',
      message: '全角括弧（）の使用は禁止されています',
      suggestion: '半角括弧()を使用してください'
    });
    hasErrors = true;
  }
});

// 結果を出力
if (hasErrors) {
  console.error('\n❌ Kotlin安全性チェック: エラーが見つかりました\n');
  console.error(`ファイル: ${filePath}\n`);

  errors.forEach(error => {
    console.error(`行 ${error.line}: ${error.type}`);
    console.error(`  ${error.message}`);
    console.error(`  💡 ${error.suggestion}\n`);
  });

  console.error('詳細は .claude/rules/kotlin-safety.md を参照してください。\n');
  process.exit(1);
} else {
  console.log(`✅ Kotlin安全性チェック: 問題なし (${filePath})`);
  process.exit(0);
}
