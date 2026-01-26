package org.sake_hack.platform

import org.sake_hack.feature.stocklist.presentation.ImageSource

/**
 * iOS用ImagePicker実装
 *
 * TODO: UIImagePickerControllerを使用した実装
 */
class IOSImagePicker : ImagePicker {
    override fun pickImage(
        source: ImageSource,
        onImageSelected: (ByteArray) -> Unit,
        onError: (String) -> Unit
    ) {
        // TODO: 実装
        // 1. UIImagePickerControllerを作成
        // 2. sourceTypeを設定（camera or photoLibrary）
        // 3. 画像取得後、NSDataに変換してByteArrayへ
        // 4. onImageSelectedコールバック

        onError("iOS画像選択機能は未実装です")
    }
}

actual fun getImagePicker(): ImagePicker {
    return IOSImagePicker()
}
