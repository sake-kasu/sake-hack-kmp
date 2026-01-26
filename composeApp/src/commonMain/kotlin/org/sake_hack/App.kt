package org.sake_hack

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.sake_hack.navigation.SakeAppNavGraph

@Composable
@Preview
fun App() {
    MaterialTheme {
        SakeAppNavGraph()
    }
}
