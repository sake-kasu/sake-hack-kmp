package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sake_hack.feature.stocklist.domain.model.Stock

/**
 * 在庫リストアイテム
 *
 * デザイン仕様:
 * - cornerRadius: 12dp
 * - padding: 12dp
 * - gap: 12dp
 * - shadow: blur 8dp, offset (0, 2), color #0000001A
 * - 画像コンテナ: 64x64dp, cornerRadius: 8dp
 * - タイトル: fontSize: 16sp, fontWeight: 700, lineHeight: 1.4
 */
@Composable
fun StockListItem(
    stock: Stock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1A000000)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 画像
            StockImage(
                imageUrl = stock.imageUrl,
                contentDescription = stock.name
            )

            // 在庫情報
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 酒名
                Text(
                    text = stock.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 1.4.sp * 16,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 大分類・産地
                Text(
                    text = buildString {
                        append(stock.mainCategory)
                        stock.region?.let {
                            append(" • ")
                            append(it)
                        }
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                // 度数・残量・価格
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StockInfoChip(
                        label = "度数",
                        value = "${stock.abv}%"
                    )
                    StockInfoChip(
                        label = "残量",
                        value = "${stock.remainingVolumePercent}%"
                    )
                    StockInfoChip(
                        label = "価格",
                        value = "¥${stock.purchasePrice.toString().reversed().chunked(3).joinToString(",").reversed()}"
                    )
                }
            }
        }
    }
}

/**
 * 在庫画像
 */
@Composable
private fun StockImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            // TODO: 画像ローディング実装（Coil使用）
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = contentDescription,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = contentDescription,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 在庫情報チップ
 */
@Composable
private fun StockInfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
