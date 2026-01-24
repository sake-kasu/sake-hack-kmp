package org.sake_hack.ui.sakelist.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.SakeTypeConstants
import org.sake_hack.feature.sakelist.presentation.SakeListIntent

/**
 * フィルターダイアログ
 * デザイン仕様: sake_list.pen - 画面6
 */
@Composable
fun FilterDialog(
    filterCriteria: FilterCriteria,
    onIntent: (SakeListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var sakeNameInput by remember(filterCriteria.sakeName) {
        mutableStateOf(filterCriteria.sakeName ?: "")
    }
    var regionInput by remember(filterCriteria.region) {
        mutableStateOf(filterCriteria.region ?: "")
    }

    Dialog(onDismissRequest = { onIntent(SakeListIntent.CloseFilterDialog) }) {
        Card(
            modifier = modifier.width(360.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // タイトル
                Text(
                    text = "フィルター",
                    style = MaterialTheme.typography.headlineSmall
                )

                // 酒名フィールド
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "酒名",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    OutlinedTextField(
                        value = sakeNameInput,
                        onValueChange = { sakeNameInput = it },
                        placeholder = { Text("酒名を入力") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 酒の種類セクション
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "酒の種類",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // チェックボックス（2列×2行）
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SakeTypeCheckbox(
                                label = SakeTypeConstants.SAKE,
                                isChecked = filterCriteria.sakeTypes.contains(SakeTypeConstants.SAKE),
                                onCheckedChange = { onIntent(SakeListIntent.ToggleSakeTypeFilter(SakeTypeConstants.SAKE)) },
                                modifier = Modifier.weight(1f)
                            )
                            SakeTypeCheckbox(
                                label = SakeTypeConstants.WHISKEY,
                                isChecked = filterCriteria.sakeTypes.contains(SakeTypeConstants.WHISKEY),
                                onCheckedChange = { onIntent(SakeListIntent.ToggleSakeTypeFilter(SakeTypeConstants.WHISKEY)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SakeTypeCheckbox(
                                label = SakeTypeConstants.WINE,
                                isChecked = filterCriteria.sakeTypes.contains(SakeTypeConstants.WINE),
                                onCheckedChange = { onIntent(SakeListIntent.ToggleSakeTypeFilter(SakeTypeConstants.WINE)) },
                                modifier = Modifier.weight(1f)
                            )
                            SakeTypeCheckbox(
                                label = SakeTypeConstants.BEER,
                                isChecked = filterCriteria.sakeTypes.contains(SakeTypeConstants.BEER),
                                onCheckedChange = { onIntent(SakeListIntent.ToggleSakeTypeFilter(SakeTypeConstants.BEER)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 産地フィールド
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "産地",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    OutlinedTextField(
                        value = regionInput,
                        onValueChange = { regionInput = it },
                        placeholder = { Text("産地を入力") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // ボタン
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { onIntent(SakeListIntent.ClearFilter) }
                    ) {
                        Text("クリア")
                    }

                    Button(
                        onClick = {
                            onIntent(SakeListIntent.UpdateSakeNameFilter(sakeNameInput.takeIf { it.isNotBlank() }))
                            onIntent(SakeListIntent.UpdateRegionFilter(regionInput.takeIf { it.isNotBlank() }))
                            onIntent(SakeListIntent.ApplyFilter)
                        }
                    ) {
                        Text("適用")
                    }
                }
            }
        }
    }
}

/**
 * 酒の種類チェックボックス
 */
@Composable
private fun SakeTypeCheckbox(
    label: String,
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange() }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
    }
}
