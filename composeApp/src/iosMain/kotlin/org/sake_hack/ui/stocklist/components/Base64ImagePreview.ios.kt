package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Base64エンコードされた画像をプレビュー表示(iOS実装)
 *
 * TODO: iOS実装を追加
 */
@Composable
actual fun Base64ImagePreview(
    dataUrl: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "画像プレビュー\n(iOS未実装)")
    }
}
