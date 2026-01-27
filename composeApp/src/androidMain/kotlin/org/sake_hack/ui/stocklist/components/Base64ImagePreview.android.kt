package org.sake_hack.ui.stocklist.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

/**
 * Base64エンコードされた画像をプレビュー表示(Android実装)
 */
@Composable
actual fun Base64ImagePreview(
    dataUrl: String,
    modifier: Modifier
) {
    var bitmap by remember(dataUrl) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var error by remember(dataUrl) { mutableStateOf<String?>(null) }

    LaunchedEffect(dataUrl) {
        try {
            // data:image/jpeg;base64,... から base64部分を抽出
            val base64String = dataUrl.substringAfter("base64,")
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            error = "画像の読み込みに失敗しました"
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "選択された画像",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            error != null -> {
                Text(text = error!!)
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }
}
