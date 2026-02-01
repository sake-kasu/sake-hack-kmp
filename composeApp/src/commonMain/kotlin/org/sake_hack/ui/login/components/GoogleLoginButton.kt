package org.sake_hack.ui.login.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Googleログインボタンコンポーネント
 *
 * デザイン仕様: docs/screens/login_logout.pen
 * - fill: $white, cornerRadius: 8dp, stroke: 1dp ($border-default)
 * - shadow: y=1, blur=3
 * - gap: 12dp, justifyContent: center, alignItems: center
 * - Googleアイコン "G": #4285F4, fontSize: 20sp
 * - "Googleでログイン": fontSize: 16sp
 *
 * @param onClick クリックハンドラー
 * @param isLoading ローディング状態
 * @param modifier Modifier
 */
@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFDDDDDD)), // $border-default
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 3.dp,
            pressedElevation = 1.dp,
            disabledElevation = 3.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF4285F4)
                )
            } else {
                // Googleアイコン "G": #4285F4
                Text(
                    text = "G",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4)
                )
            }

            Text(
                text = "Googleでログイン",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isLoading) Color.Gray else Color.Black
            )
        }
    }
}
