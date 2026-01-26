package org.sake_hack.platform

import android.content.Context
import org.sake_hack.feature.stocklist.presentation.ImageSource

/**
 * Android用ImagePicker実装
 *
 * TODO: ActivityResultAPI、Permissions、カメラ/ギャラリーアクセスを実装
 */
class AndroidImagePicker(private val context: Context) : ImagePicker {
    override fun pickImage(
        source: ImageSource,
        onImageSelected: (ByteArray) -> Unit,
        onError: (String) -> Unit
    ) {
        // TODO: 実装
        // 1. Permissionsチェック（カメラ/ストレージ）
        // 2. ActivityResultAPIでカメラまたはギャラリー起動
        // 3. 画像取得後、ByteArrayに変換してonImageSelectedコールバック

        onError("Android画像選択機能は未実装です")
    }
}

actual fun getImagePicker(): ImagePicker {
    // TODO: Contextを取得する仕組みが必要
    // Koinで注入するか、ComposeのLocalContextCurrentを使用
    throw UnsupportedOperationException("Android ImagePicker requires Context")
}
