package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Android用トリミングビュー
 *
 * TODO: uCropまたはAndroid-Image-Cropperライブラリを統合
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
        // TODO: AndroidViewでuCropのビューを統合
        // 現在は仮実装
        Text(
            text = "Android トリミングビュー\n(uCrop統合予定)",
            color = Color.White
        )
    }
}
