package org.sake_hack.ui.logout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ログアウト確認ダイアログ
 *
 * デザイン仕様: docs/screens/login_logout.pen
 * - サイズ: 320dp幅
 * - fill: $white, cornerRadius: 16dp, shadow: y=8 blur=24
 * - layout: vertical, gap: 20dp, padding: 24dp
 * - dialogIcon: 32×32, fill: $sun-800
 * - dialogTitle: "ログアウトしますか？", fontSize: 18sp, fontWeight: 700
 * - dialogDesc: fontSize: 14sp, lineHeight: 1.5, width: 272dp
 * - dialogActions: gap: 12dp, justifyContent: end
 * - cancelBtn: Secondary Button
 * - logoutConfirmBtn: height: 48dp, fill: $sun-800, cornerRadius: 8dp, padding: [12, 20]
 *
 * @param onDismiss ダイアログを閉じるコールバック
 * @param onConfirm ログアウト確定コールバック
 */
@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 24.dp,
        text = {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // アイコン: 32×32
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFFD84315) // $sun-800
                )

                // タイトル: fontSize=18sp, fontWeight=700
                Text(
                    text = "ログアウトしますか？",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                // 説明: fontSize=14sp, lineHeight=1.5, width=272dp
                Text(
                    text = "ログアウトすると、再度ログインが\n必要になります。",
                    fontSize = 14.sp,
                    lineHeight = (14 * 1.5).sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.width(272.dp)
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // キャンセルボタン
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "キャンセル",
                        fontSize = 14.sp,
                        color = Color(0xFF1A1A1A)
                    )
                }

                // ログアウトボタン: height=48dp, fill=$sun-800
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD84315) // $sun-800
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "ログアウト",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    )
}
