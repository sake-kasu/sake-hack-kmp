package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 在庫一覧TopAppBar
 *
 * デザイン仕様:
 * - height: 64dp
 * - padding: 0, 16dp
 * - gap: 16dp
 */
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListTopBar(
    filterCount: Int,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "在庫一覧",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                // フィルターボタン
                IconButton(onClick = onFilterClick) {
                    BadgedBox(
                        badge = {
                            if (filterCount > 0) {
                                Badge {
                                    Text(filterCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "フィルター"
                        )
                    }
                }

                // ソートボタン
                IconButton(onClick = onSortClick) {
                    Icon(
                        imageVector = Icons.Outlined.Sort,
                        contentDescription = "並べ替え"
                    )
                }
            }
        },
        modifier = modifier.height(64.dp)
    )
}
