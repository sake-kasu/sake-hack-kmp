package org.sake_hack.ui.sakelist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.SakeSortCriteria
import org.sake_hack.feature.sakelist.domain.model.SakeSortField

/**
 * 酒アクションバー(フィルター・ソートボタン)
 *
 * デザイン仕様:
 * - gap: 12dp
 * - ボタン: cornerRadius: 8dp, padding: 8dp,12dp, stroke: border-default, thickness: 1
 */
@Suppress("DEPRECATION")
@Composable
fun SakeActionBar(
    filterCriteria: FilterCriteria,
    sortCriteria: SakeSortCriteria,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // フィルターボタン
        OutlinedButton(
            onClick = onFilterClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (filterCriteria.isActive()) {
                    "フィルター (${filterCriteria.activeFilterCount()})"
                } else {
                    "フィルター"
                },
                fontSize = 14.sp
            )
        }

        // ソートボタン
        OutlinedButton(
            onClick = onSortClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Icon(
                imageVector = Icons.Outlined.Sort,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildString {
                    append("並べ替え: ")
                    append(
                        when (sortCriteria.field) {
                            SakeSortField.NAME -> "酒名"
                            SakeSortField.TYPE -> "種類"
                            SakeSortField.REGION -> "産地"
                        }
                    )
                },
                fontSize = 14.sp
            )
        }
    }
}
