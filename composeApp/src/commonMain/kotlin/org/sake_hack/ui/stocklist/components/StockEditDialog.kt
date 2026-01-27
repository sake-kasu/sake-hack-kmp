package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sake_hack.feature.stocklist.domain.model.StockEditField
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockFieldUpdate
import org.sake_hack.feature.stocklist.domain.validation.StockValidator

/**
 * 在庫編集ダイアログ
 *
 * 11項目のフォームフィールド:
 * 1. 名前（必須、最大100文字、記号禁止）
 * 2. ふりがな（必須、最大100文字、記号禁止）
 * 3. 大分類（必須、選択式）
 * 4. 小分類（オプショナル、最大100文字、記号禁止）
 * 5. 産地（オプショナル、コンボボックス）
 * 6. 度数（必須、整数、0〜100）
 * 7. 購入時容量(mL)（必須、整数、0〜10000）
 * 8. 残容量（必須、選択式、0,25,50,75,100）
 * 9. 購入時価格（必須、整数、0〜1000000）
 * 10. 自由記述（オプショナル、最大500文字、記号・絵文字OK）
 * 11. 画像（オプショナル、ファイルサイズ10MB以下）
 */
@Composable
fun StockEditDialog(
    editingStock: StockEditRequest,
    validationErrors: Map<StockEditField, String>,
    isSaving: Boolean,
    onUpdateField: (StockFieldUpdate) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = modifier.width(360.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                // ヘッダー
                StockEditHeader(
                    title = "在庫を編集",
                    onClose = onCancel
                )

                // フォーム
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 凡例
                    Text(
                        text = "* は必須項目です",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 画像
                    StockEditImageField(
                        imageUrl = editingStock.imageUrl,
                        onClick = onImageClick,
                        error = validationErrors[StockEditField.IMAGE]
                    )

                    // 名前（必須）
                    StockEditTextField(
                        label = "名前",
                        value = editingStock.name,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateName(it)) },
                        error = validationErrors[StockEditField.NAME],
                        isRequired = true,
                        placeholder = "酒名を入力"
                    )

                    // ふりがな（必須）
                    StockEditTextField(
                        label = "ふりがな",
                        value = editingStock.kana,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateKana(it)) },
                        error = validationErrors[StockEditField.KANA],
                        isRequired = true,
                        placeholder = "ふりがなを入力"
                    )

                    // 大分類（必須、選択式）
                    StockEditDropdownField(
                        label = "大分類",
                        value = editingStock.mainCategory,
                        options = StockValidator.MAIN_CATEGORIES,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateMainCategory(it)) },
                        error = validationErrors[StockEditField.MAIN_CATEGORY],
                        isRequired = true,
                        placeholder = "大分類を選択"
                    )

                    // 小分類（オプショナル）
                    StockEditTextField(
                        label = "小分類",
                        value = editingStock.subCategory ?: "",
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateSubCategory(it.ifBlank { null })) },
                        error = validationErrors[StockEditField.SUB_CATEGORY],
                        isRequired = false,
                        placeholder = "小分類を入力"
                    )

                    // 産地（オプショナル、コンボボックス）
                    StockEditComboBoxField(
                        label = "産地",
                        value = editingStock.region ?: "",
                        options = StockValidator.REGIONS,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateRegion(it.ifBlank { null })) },
                        error = validationErrors[StockEditField.REGION],
                        isRequired = false,
                        placeholder = "産地を選択または入力"
                    )

                    // 度数（必須、整数）
                    StockEditNumberField(
                        label = "度数",
                        value = editingStock.abv.toString(),
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateAbv(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.ABV],
                        isRequired = true,
                        placeholder = "度数を入力（0〜100）",
                        suffix = "%"
                    )

                    // 購入時容量(mL)（必須、整数）
                    StockEditNumberField(
                        label = "購入時容量(mL)",
                        value = editingStock.initialVolume.toString(),
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateInitialVolume(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.INITIAL_VOLUME],
                        isRequired = true,
                        placeholder = "購入時容量を入力（0〜10000）",
                        suffix = "mL"
                    )

                    // 残容量（必須、選択式）
                    StockEditDropdownField(
                        label = "残容量",
                        value = editingStock.remainingVolumePercent.toString(),
                        options = StockValidator.REMAINING_VOLUME_OPTIONS.map { it.toString() },
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateRemainingVolume(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.REMAINING_VOLUME],
                        isRequired = true,
                        placeholder = "残容量を選択",
                        suffix = "%"
                    )

                    // 購入時価格（必須、整数）
                    StockEditNumberField(
                        label = "購入時価格",
                        value = editingStock.purchasePrice.toString(),
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdatePurchasePrice(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.PURCHASE_PRICE],
                        isRequired = true,
                        placeholder = "購入時価格を入力（0〜1000000）",
                        prefix = "¥"
                    )

                    // 自由記述（オプショナル、最大500文字）
                    StockEditTextAreaField(
                        label = "メモ",
                        value = editingStock.notes ?: "",
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateNotes(it.ifBlank { null })) },
                        error = validationErrors[StockEditField.NOTES],
                        isRequired = false,
                        placeholder = "メモを入力"
                    )
                }

                // ボタン
                StockEditButtons(
                    isSaving = isSaving,
                    onSave = onSave,
                    onCancel = onCancel
                )
            }
        }
    }
}

/**
 * 編集ダイアログヘッダー
 */
@Composable
private fun StockEditHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "閉じる"
            )
        }
    }
}

/**
 * 画像フィールド
 */
@Composable
private fun StockEditImageField(
    imageUrl: String?,
    onClick: () -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "画像",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onClick) {
                        Text("画像を選択")
                    }
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * テキストフィールド
 */
@Composable
internal fun StockEditTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRequired) {
            RequiredLabel(label)
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 数値フィールド
 */
@Composable
internal fun StockEditNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean,
    placeholder: String,
    prefix: String? = null,
    suffix: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRequired) {
            RequiredLabel(label)
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            prefix = prefix?.let { { Text(it) } },
            suffix = suffix?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = error != null
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * ドロップダウンフィールド
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StockEditDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean,
    placeholder: String,
    suffix: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRequired) {
            RequiredLabel(label)
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value + (suffix ?: ""),
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                isError = error != null
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * コンボボックスフィールド（選択+自由入力）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StockEditComboBoxField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRequired) {
            RequiredLabel(label)
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                singleLine = true,
                isError = error != null
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * テキストエリアフィールド
 */
@Composable
internal fun StockEditTextAreaField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRequired) {
            RequiredLabel(label)
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
            maxLines = 5,
            isError = error != null
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 保存・キャンセルボタン
 */
@Composable
private fun StockEditButtons(
    isSaving: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            enabled = !isSaving
        ) {
            Text("キャンセル")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("保存")
        }
    }
}

/**
 * 必須ラベル
 */
@Composable
private fun RequiredLabel(text: String) {
    Row {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = " *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
    }
}
