package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sake_hack.feature.stocklist.domain.model.Stock

/**
 * 在庫詳細ダイアログ(読み取り専用)
 *
 * デザイン仕様:
 * - width: 360dp
 * - cornerRadius: 28dp
 * - shadow: blur 24dp, offset (0, 8), color #0000003D
 * - ヘッダー: padding 16dp,20dp
 * - ボディ: padding 0,20dp,20dp,20dp, gap: 16dp
 * - 画像: height 200dp, cornerRadius: 16dp
 * - 情報グリッド: gap 12dp(行間)、16dp(列間)
 */
@Composable
fun StockDetailDialog(
    stock: Stock,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .width(360.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0x3D000000),
                    spotColor = Color(0x3D000000)
                ),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                // ヘッダー
                StockDetailHeader(
                    stockName = stock.name,
                    onClose = onDismiss,
                    onEdit = onEdit
                )

                // ボディ
                Column(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 画像
                    StockDetailImage(imageUrl = stock.imageUrl)

                    // 基本情報
                    StockDetailInfoGrid(stock = stock)

                    // 自由記述
                    stock.notes?.let { notes ->
                        StockDetailNotes(notes = notes)
                    }
                }
            }
        }
    }
}

/**
 * 詳細ダイアログヘッダー
 */
@Composable
private fun StockDetailHeader(
    stockName: String,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "閉じる"
            )
        }

        Text(
            text = stockName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
        )

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "編集"
            )
        }
    }
}

/**
 * 詳細画像
 */
@Composable
private fun StockDetailImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            // TODO: 画像ローディング実装（Coil使用）
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 詳細情報グリッド
 */
@Composable
private fun StockDetailInfoGrid(
    stock: Stock,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1行目: よみがな
        DetailInfoRow(
            label = "よみがな",
            value = stock.kana
        )

        // 2行目: 大分類・小分類
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailInfoRow(
                label = "大分類",
                value = stock.mainCategory,
                modifier = Modifier.weight(1f)
            )
            stock.subCategory?.let {
                DetailInfoRow(
                    label = "小分類",
                    value = it,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3行目: 産地
        stock.region?.let {
            DetailInfoRow(
                label = "産地",
                value = it
            )
        }

        // 4行目: 度数・購入時容量
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailInfoRow(
                label = "度数",
                value = "${stock.abv}%",
                modifier = Modifier.weight(1f)
            )
            DetailInfoRow(
                label = "購入時容量",
                value = "${stock.initialVolume}mL",
                modifier = Modifier.weight(1f)
            )
        }

        // 5行目: 残容量・購入時価格
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailInfoRow(
                label = "残容量",
                value = "${stock.remainingVolumePercent}%",
                modifier = Modifier.weight(1f)
            )
            DetailInfoRow(
                label = "購入時価格",
                value = "¥${stock.purchasePrice.toString().reversed().chunked(3).joinToString(",").reversed()}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 詳細情報行
 */
@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 自由記述
 */
@Composable
private fun StockDetailNotes(
    notes: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "メモ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = notes,
                fontSize = 14.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
