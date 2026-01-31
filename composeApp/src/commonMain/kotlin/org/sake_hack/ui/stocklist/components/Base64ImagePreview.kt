package org.sake_hack.ui.stocklist.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Base64エンコードされた画像をプレビュー表示
 *
 * プラットフォーム固有の実装で画像をデコード・表示
 */
@Composable
expect fun Base64ImagePreview(
    dataUrl: String,
    modifier: Modifier = Modifier
)
