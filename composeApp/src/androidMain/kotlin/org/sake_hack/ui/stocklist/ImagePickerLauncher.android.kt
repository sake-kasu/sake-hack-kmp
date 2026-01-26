package org.sake_hack.ui.stocklist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sake_hack.feature.stocklist.presentation.ImageSource
import java.io.ByteArrayOutputStream

/**
 * Android用画像選択Launcher
 */
@Composable
actual fun rememberImagePickerLauncher(
    onImageSelected: (ByteArray) -> Unit,
    onError: (String) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current

    // ギャラリーからの選択
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val byteArray = inputStream?.use { stream ->
                    val buffer = ByteArrayOutputStream()
                    val data = ByteArray(1024)
                    var count: Int
                    while (stream.read(data).also { count = it } != -1) {
                        buffer.write(data, 0, count)
                    }
                    buffer.toByteArray()
                }
                if (byteArray != null) {
                    onImageSelected(byteArray)
                } else {
                    onError("画像の読み込みに失敗しました")
                }
            } catch (e: Exception) {
                onError("画像の読み込みに失敗しました: ${e.message}")
            }
        } else {
            onError("画像が選択されませんでした")
        }
    }

    // カメラでの撮影
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, stream)
                val byteArray = stream.toByteArray()
                onImageSelected(byteArray)
            } catch (e: Exception) {
                onError("画像の変換に失敗しました: ${e.message}")
            }
        } else {
            onError("写真が撮影されませんでした")
        }
    }

    return remember {
        object : ImagePickerLauncher {
            override fun launch(source: ImageSource) {
                when (source) {
                    ImageSource.CAMERA -> cameraLauncher.launch(null)
                    ImageSource.GALLERY -> galleryLauncher.launch("image/*")
                }
            }
        }
    }
}
