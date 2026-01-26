package org.sake_hack.ui.stocklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.sake_hack.feature.stocklist.presentation.ImageSource

/**
 * iOS用画像選択Launcher
 *
 * TODO: UIImagePickerControllerを使用した実装
 * UIKitViewを使用してSwift/Objective-Cのコードと連携が必要
 */
@Composable
actual fun rememberImagePickerLauncher(
    onImageSelected: (ByteArray) -> Unit,
    onError: (String) -> Unit
): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            override fun launch(source: ImageSource) {
                // TODO: UIImagePickerControllerの実装
                // 1. UIImagePickerControllerを作成
                // 2. sourceType設定（camera or photoLibrary）
                // 3. delegateでコールバック受信
                // 4. UIImageをNSDataに変換してByteArrayへ
                onError("iOS画像選択機能は未実装です")
            }
        }
    }
}
