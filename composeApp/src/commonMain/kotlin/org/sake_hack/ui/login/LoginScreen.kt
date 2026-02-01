package org.sake_hack.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.feature.auth.presentation.LoginIntent
import org.sake_hack.feature.auth.presentation.LoginViewModel
import org.sake_hack.ui.login.components.GoogleLoginButton

/**
 * ログイン画面
 *
 * デザイン仕様: docs/screens/login_logout.pen (Android セクション)
 *
 * @param onLoginSuccess ログイン成功時のコールバック
 * @param viewModel LoginViewModel
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ログイン成功時の処理
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    // エラー表示
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error.message ?: "エラーが発生しました")
            viewModel.handleIntent(LoginIntent.ClearError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // StatusBar: 24dp
            Spacer(modifier = Modifier.height(24.dp))

            // CenterContent: y=200dp (StatusBar含め176dp + 24dp)
            Spacer(modifier = Modifier.height(176.dp))

            // デザイン仕様: x=46, width=320dp, gap=32dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ロゴアイコン: 64×64dp
                Icon(
                    imageVector = Icons.Rounded.LocalBar,
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF8B4513) // $wood-700
                )

                // タイトル: fontSize=28sp, fontWeight=700
                Text(
                    text = "酒アプリ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A) // $text-body
                )

                // 説明: fontSize=14sp, lineHeight=1.6, width=280dp
                Text(
                    text = "お気に入りの日本酒を\n記録・管理しましょう",
                    fontSize = 14.sp,
                    lineHeight = (14 * 1.6).sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B6B6B), // $text-description
                    modifier = Modifier.width(280.dp)
                )
            }

            // y=480dp までの余白
            Spacer(modifier = Modifier.height(48.dp))

            // Googleログインボタン: 320×52dp
            GoogleLoginButton(
                onClick = { viewModel.handleIntent(LoginIntent.LoginWithGoogle) },
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 46.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Divider: gap=16dp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
                Text(
                    text = "または",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E) // $text-placeholder
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ゲストテキスト: fontSize=14sp, fill=$text-link
            Text(
                text = "ゲストとして利用する",
                fontSize = 14.sp,
                color = Color(0xFF1976D2), // $text-link
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.handleIntent(LoginIntent.LoginAsGuest)
                    }
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // フッター: fontSize=11sp, fill=$text-placeholder
            Text(
                text = "ログインすると利用規約に同意したものとみなされます",
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E), // $text-placeholder
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
            )
        }

        // Snackbar ホスト
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
