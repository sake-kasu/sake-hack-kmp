package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria
import org.sake_hack.feature.stocklist.domain.validation.StockValidator

/**
 * 在庫フィルターダイアログ
 *
 * デザイン仕様:
 * - width: 360dp
 * - padding: 20dp
 * - gap: 20dp
 */
@Composable
fun StockFilterDialog(
    filterCriteria: StockFilterCriteria,
    onUpdateName: (String?) -> Unit,
    onToggleCategory: (String) -> Unit,
    onUpdateRegion: (String?) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(360.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // 酒名フィルター
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "酒名",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = filterCriteria.name ?: "",
                        onValueChange = { onUpdateName(it.ifBlank { null }) },
                        placeholder = { Text("酒名で検索") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 種類フィルター
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "種類",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    StockValidator.MAIN_CATEGORIES.forEach { category ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = filterCriteria.categories.contains(category),
                                onCheckedChange = { onToggleCategory(category) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // 産地フィルター
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "産地",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = filterCriteria.region ?: "",
                        onValueChange = { onUpdateRegion(it.ifBlank { null }) },
                        placeholder = { Text("産地で検索") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // ボタン
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("キャンセル")
                    }
                    Button(
                        onClick = onApply,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("適用")
                    }
                }
            }
        }
    }
}
