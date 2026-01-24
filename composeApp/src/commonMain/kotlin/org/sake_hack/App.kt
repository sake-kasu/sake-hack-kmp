package org.sake_hack

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.sake_hack.navigation.SakeAppNavGraph

@Composable
@Preview
fun App() {
    KoinContext {
        MaterialTheme {
            SakeAppNavGraph()
        }
    }
}
