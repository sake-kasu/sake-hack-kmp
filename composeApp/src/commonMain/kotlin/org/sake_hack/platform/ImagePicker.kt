package org.sake_hack.platform

import org.sake_hack.feature.stocklist.presentation.ImageSource

/**
 * プラットフォーム固有の画像選択機能
 */
interface ImagePicker {
    /**
     * 画像を選択
     *
     * @param source 画像ソース（カメラまたはギャラリー）
     * @param onImageSelected 画像選択時のコールバック
     * @param onError エラー時のコールバック
     */
    fun pickImage(
        source: ImageSource,
        onImageSelected: (ByteArray) -> Unit,
        onError: (String) -> Unit
    )
}

/**
 * ImagePickerのインスタンスを取得
 */
expect fun getImagePicker(): ImagePicker
