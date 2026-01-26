package org.sake_hack.ui.stocklist

import androidx.compose.runtime.Composable
import org.sake_hack.feature.stocklist.presentation.ImageSource

/**
 * 画像選択Launcherインターフェース
 */
interface ImagePickerLauncher {
    /**
     * 画像選択を起動
     *
     * @param source 画像ソース（カメラまたはギャラリー）
     */
    fun launch(source: ImageSource)
}

/**
 * プラットフォーム固有の画像選択Launcherを取得
 *
 * @param onImageSelected 画像選択成功時のコールバック
 * @param onError エラー時のコールバック
 */
@Composable
expect fun rememberImagePickerLauncher(
    onImageSelected: (ByteArray) -> Unit,
    onError: (String) -> Unit
): ImagePickerLauncher
