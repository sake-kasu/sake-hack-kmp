package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sake_hack.feature.stocklist.domain.model.StockSortCriteria
import org.sake_hack.feature.stocklist.domain.model.StockSortField

/**
 * 在庫ソートメニュー
 *
 * デザイン仕様:
 * - width: 180dp
 * - cornerRadius: 8dp
 * - shadow: blur 16dp, offset (0, 4), color #00000026
 * - オプション: padding 12dp,16dp, gap: 8dp
 */
@Composable
fun StockSortMenu(
    currentSortCriteria: StockSortCriteria,
    onSortSelected: (StockSortCriteria) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .width(180.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                    ambientColor = Color(0x26000000),
                    spotColor = Color(0x26000000)
                ),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                SortOption(
                    label = "度数",
                    field = StockSortField.ABV,
                    currentSortCriteria = currentSortCriteria,
                    onSortSelected = onSortSelected
                )
                SortOption(
                    label = "酒名",
                    field = StockSortField.NAME,
                    currentSortCriteria = currentSortCriteria,
                    onSortSelected = onSortSelected
                )
                SortOption(
                    label = "残量",
                    field = StockSortField.REMAINING_VOLUME,
                    currentSortCriteria = currentSortCriteria,
                    onSortSelected = onSortSelected
                )
                SortOption(
                    label = "金額",
                    field = StockSortField.PRICE,
                    currentSortCriteria = currentSortCriteria,
                    onSortSelected = onSortSelected
                )
            }
        }
    }
}

/**
 * ソートオプション
 */
@Composable
private fun SortOption(
    label: String,
    field: StockSortField,
    currentSortCriteria: StockSortCriteria,
    onSortSelected: (StockSortCriteria) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = currentSortCriteria.field == field

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSortSelected(
                    StockSortCriteria(
                        field = field,
                        ascending = if (isSelected) !currentSortCriteria.ascending else true
                    )
                )
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Text(
                text = if (currentSortCriteria.ascending) "↑" else "↓",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
