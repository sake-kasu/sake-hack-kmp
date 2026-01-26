package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 画像トリミング画面
 *
 * プラットフォーム固有のトリミング機能をラップ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropScreen(
    imageData: ByteArray,
    onCropComplete: (ByteArray) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ImageCropTopBar(
                onCancel = onCancel,
                onComplete = {
                    // プラットフォーム固有のトリミング実行
                    // TODO: 実際のトリミング処理を実装
                    onCropComplete(imageData)
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // プラットフォーム固有のトリミングビュー
            // TODO: expect/actualでAndroid/iOSのトリミングビューを統合
            ImageCropView(
                imageData = imageData,
                modifier = Modifier.fillMaxSize()
            )

            // ツールバー
            ImageCropToolbar(
                onRotate = {
                    // TODO: 回転処理
                },
                onReset = {
                    // TODO: リセット処理
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
}

/**
 * トリミング画面トップバー
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCropTopBar(
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "トリミング",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "戻る"
                )
            }
        },
        actions = {
            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "完了"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

/**
 * トリミングビュー（expect/actual）
 */
@Composable
expect fun ImageCropView(
    imageData: ByteArray,
    modifier: Modifier = Modifier
)

/**
 * トリミングツールバー
 */
@Composable
private fun ImageCropToolbar(
    onRotate: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.7f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 回転ボタン
            ImageCropToolButton(
                icon = Icons.Default.RotateRight,
                label = "回転",
                onClick = onRotate
            )

            // リセットボタン
            ImageCropToolButton(
                icon = Icons.Default.Refresh,
                label = "リセット",
                onClick = onReset
            )
        }
    }
}

/**
 * ツールバーボタン
 */
@Composable
private fun ImageCropToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}
