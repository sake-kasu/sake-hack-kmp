package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * iOS用トリミングビュー
 *
 * TODO: TOCropViewControllerまたはUIImagePickerControllerのトリミング機能を統合
 */
@Composable
actual fun ImageCropView(
    imageData: ByteArray,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TODO: UIKitViewでTOCropViewControllerを統合
        // 現在は仮実装
        Text(
            text = "iOS トリミングビュー\n(TOCropViewController統合予定)",
            color = Color.White
        )
    }
}
