package org.sake_hack.ui.sakelist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.sake_hack.feature.sakelist.domain.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SakeListTopBar(
    filterCount: Int,
    sortOption: SortOption,
    isSortMenuOpen: Boolean,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onDismissSortMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column {
            // TopAppBar - タイトルのみ
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Menu,
                            contentDescription = "メニュー",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "酒一覧",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            )

            // ActionBar - フィルター・並べ替えボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // フィルターボタン
                OutlinedButton(
                    onClick = onFilterClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "フィルター",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    if (filterCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Badge {
                            Text(
                                text = filterCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                // 並べ替えボタン
                OutlinedButton(
                    onClick = onSortClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "並べ替え",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // 並べ替えメニューポップアップ
        if (isSortMenuOpen) {
            SortMenuPopup(
                sortOption = sortOption,
                onSelect = onSortSelect,
                onDismiss = onDismissSortMenu
            )
        }
    }
}

/**
 * 並べ替えメニューポップアップ
 * デザイン仕様: sake_list.pen - 並べ替えメニュー
 */
@Composable
private fun SortMenuPopup(
    sortOption: SortOption,
    onSelect: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(16, 120),
        properties = PopupProperties(focusable = true)
    ) {
        Card(
            modifier = Modifier.width(180.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .widthIn(min = 180.dp)
            ) {
                SortOption.values().forEachIndexed { index, option ->
                    val isSelected = option == sortOption
                    SortMenuItem(
                        option = option,
                        isSelected = isSelected,
                        onClick = {
                            onSelect(option)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SortMenuItem(
    option: SortOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textStyle = if (isSelected) {
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp
        )
    }

    Surface(
        onClick = onClick,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // チェックアイコン or スペーサー
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Spacer(modifier = Modifier.size(20.dp))
            }

            Text(
                text = option.displayName,
                style = textStyle
            )
        }
    }
}
