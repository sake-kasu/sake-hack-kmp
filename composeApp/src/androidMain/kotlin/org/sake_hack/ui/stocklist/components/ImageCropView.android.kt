package org.sake_hack.ui.stocklist.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale

/**
 * Android用トリミングビュー
 *
 * 基本的な画像表示とズーム/パン機能、クロップ枠を実装
 */
@Composable
actual fun ImageCropView(
    imageData: ByteArray,
    modifier: Modifier
) {
    // ByteArrayからBitmapを生成
    val bitmap = remember(imageData) {
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    // ズーム・パンの状態
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // 画像の実際の表示サイズ
    var imageDisplayWidth by remember { mutableStateOf(0f) }
    var imageDisplayHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            // 画像表示
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "トリミング対象画像",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
                    .pointerInput(Unit) {
                        // ContentScale.Fitの実際の表示サイズを計算
                        val imageAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        val containerAspectRatio = size.width / size.height

                        if (imageAspectRatio > containerAspectRatio) {
                            // 画像が横長、幅に合わせる
                            imageDisplayWidth = size.width.toFloat()
                            imageDisplayHeight = size.width / imageAspectRatio
                        } else {
                            // 画像が縦長、高さに合わせる
                            imageDisplayHeight = size.height.toFloat()
                            imageDisplayWidth = size.height * imageAspectRatio
                        }

                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)

                            // クロップ枠のサイズ（正方形、画面幅）
                            val cropSize = size.width.toFloat()

                            // スケール後の画像サイズ
                            val scaledWidth = imageDisplayWidth * scale
                            val scaledHeight = imageDisplayHeight * scale

                            // 移動可能な範囲を計算
                            // 画像がクロップ枠より大きい場合：はみ出た分だけ動かせる
                            // 画像がクロップ枠より小さい場合：クロップ枠内で自由に動かせる
                            val maxX = kotlin.math.abs(scaledWidth - cropSize) / 2
                            val maxY = kotlin.math.abs(scaledHeight - cropSize) / 2

                            offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                            offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                        }
                    },
                contentScale = ContentScale.Fit
            )

            // クロップ枠のオーバーレイ
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // クロップ枠のサイズ(正方形、横幅いっぱい)
                val cropSize = canvasWidth

                // クロップ枠の位置(中央)
                val cropLeft = 0f
                val cropTop = (canvasHeight - cropSize) / 2

                // 暗いオーバーレイ(クロップ枠の外側)
                // 上
                drawRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(0f, 0f),
                    size = Size(canvasWidth, cropTop)
                )
                // 下
                drawRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(0f, cropTop + cropSize),
                    size = Size(canvasWidth, canvasHeight - cropTop - cropSize)
                )

                // クロップ枠の白い枠線
                drawRect(
                    color = Color.White,
                    topLeft = Offset(cropLeft, cropTop),
                    size = Size(cropSize, cropSize),
                    style = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )

                // 三分割グリッド線
                val gridLineColor = Color.White.copy(alpha = 0.5f)
                // 縦線
                for (i in 1..2) {
                    val x = cropLeft + cropSize * i / 3
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, cropTop),
                        end = Offset(x, cropTop + cropSize),
                        strokeWidth = 1f
                    )
                }
                // 横線
                for (i in 1..2) {
                    val y = cropTop + cropSize * i / 3
                    drawLine(
                        color = gridLineColor,
                        start = Offset(cropLeft, y),
                        end = Offset(cropLeft + cropSize, y),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}
