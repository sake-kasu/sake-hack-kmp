package org.sake_hack.ui.stocklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sake_hack.feature.stocklist.domain.model.StockEditField
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockFieldUpdate
import org.sake_hack.feature.stocklist.domain.validation.StockValidator

/**
 * 在庫追加ダイアログ
 *
 * StockEditDialogと同じ構造で、タイトルを「在庫を追加」に変更
 * 初期値: 空欄（残容量のみデフォルト100%）
 */
@Composable
fun StockCreateDialog(
    creatingStock: StockEditRequest,
    validationErrors: Map<StockEditField, String>,
    isCreating: Boolean,
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
                StockCreateHeader(
                    title = "在庫を追加",
                    onClose = onCancel
                )

                // ボディ（スクロール可能）
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 凡例
                    Text(
                        text = "* は必須項目です",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 画像フィールド
                    StockCreateImageField(
                        imageUrl = creatingStock.imageUrl,
                        onImageClick = onImageClick,
                        error = validationErrors[StockEditField.IMAGE]
                    )

                    // 名前（必須）
                    StockEditTextField(
                        label = "名前",
                        value = creatingStock.name,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateName(it)) },
                        error = validationErrors[StockEditField.NAME],
                        isRequired = true,
                        placeholder = "酒名を入力"
                    )

                    // ふりがな（必須）
                    StockEditTextField(
                        label = "ふりがな",
                        value = creatingStock.kana,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateKana(it)) },
                        error = validationErrors[StockEditField.KANA],
                        isRequired = true,
                        placeholder = "ふりがなを入力"
                    )

                    // 大分類（必須、選択式）
                    StockEditDropdownField(
                        label = "大分類",
                        value = creatingStock.mainCategory,
                        options = StockValidator.MAIN_CATEGORIES,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateMainCategory(it)) },
                        error = validationErrors[StockEditField.MAIN_CATEGORY],
                        isRequired = true,
                        placeholder = "大分類を選択"
                    )

                    // 小分類（オプショナル）
                    StockEditTextField(
                        label = "小分類",
                        value = creatingStock.subCategory ?: "",
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateSubCategory(it.ifBlank { null })) },
                        error = validationErrors[StockEditField.SUB_CATEGORY],
                        isRequired = false,
                        placeholder = "小分類を入力"
                    )

                    // 産地（オプショナル、コンボボックス）
                    StockEditComboBoxField(
                        label = "産地",
                        value = creatingStock.region ?: "",
                        options = StockValidator.REGIONS,
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateRegion(it.ifBlank { null })) },
                        error = validationErrors[StockEditField.REGION],
                        isRequired = false,
                        placeholder = "産地を選択または入力"
                    )

                    // 度数（必須、整数）
                    StockEditNumberField(
                        label = "度数",
                        value = creatingStock.abv.toString(),
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateAbv(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.ABV],
                        isRequired = true,
                        placeholder = "度数を入力(0〜100)",
                        suffix = "%"
                    )

                    // 購入時容量(mL)（必須、整数）
                    StockEditNumberField(
                        label = "購入時容量(mL)",
                        value = creatingStock.initialVolume.toString(),
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateInitialVolume(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.INITIAL_VOLUME],
                        isRequired = true,
                        placeholder = "購入時容量を入力(0〜10000)",
                        suffix = "mL"
                    )

                    // 残容量（必須、選択式）
                    StockEditDropdownField(
                        label = "残容量",
                        value = creatingStock.remainingVolumePercent.toString(),
                        options = StockValidator.REMAINING_VOLUME_OPTIONS.map { it.toString() },
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateRemainingVolume(it.toIntOrNull() ?: 100)) },
                        error = validationErrors[StockEditField.REMAINING_VOLUME],
                        isRequired = true,
                        placeholder = "残容量を選択",
                        suffix = "%"
                    )

                    // 購入時価格（必須、整数）
                    StockEditNumberField(
                        label = "購入時価格",
                        value = creatingStock.purchasePrice.toString(),
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdatePurchasePrice(it.toIntOrNull() ?: 0)) },
                        error = validationErrors[StockEditField.PURCHASE_PRICE],
                        isRequired = true,
                        placeholder = "購入時価格を入力(0〜1000000)",
                        prefix = "¥"
                    )

                    // 自由記述（オプショナル、最大500文字）
                    StockEditTextAreaField(
                        label = "メモ",
                        value = creatingStock.notes ?: "",
                        onValueChange = { onUpdateField(StockFieldUpdate.UpdateNotes(it.ifBlank { null })) },
                        error = validationErrors[StockEditField.NOTES],
                        isRequired = false,
                        placeholder = "メモを入力"
                    )
                }

                // ボタン
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // キャンセルボタン
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        enabled = !isCreating
                    ) {
                        Text("キャンセル")
                    }

                    // 追加ボタン
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        enabled = !isCreating
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("追加")
                    }
                }
            }
        }
    }
}

/**
 * 追加ダイアログヘッダー
 */
@Composable
private fun StockCreateHeader(
    title: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
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
 * 追加ダイアログ画像フィールド
 */
@Composable
private fun StockCreateImageField(
    imageUrl: String?,
    onImageClick: () -> Unit,
    error: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "画像",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                // TODO: Coil統合時に画像表示実装
                Text(
                    text = "画像プレビュー",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onImageClick) {
                        Text("画像を選択")
                    }
                }
            }
        }

        error?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
