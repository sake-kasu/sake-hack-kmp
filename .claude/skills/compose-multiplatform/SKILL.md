---
name: compose-multiplatform
description: Compose Multiplatform UI開発パターン
---

# Compose Multiplatform スキル

## 使用タイミング

Compose Multiplatform で UI を実装する際に常に参照してください。

## コンポーネント構成の原則

### 小さく、再利用可能なコンポーネント

- プレビュー可能な小さなコンポーネントを作成
- Material 3 を使用
- デザイン仕様（.penファイル）に厳密に準拠

```kotlin
// ✅ 良い例 - 小さく、再利用可能
@Composable
fun SakeCard(
    sake: Sake,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = sake.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = sake.type,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// プレビュー
@Preview
@Composable
fun SakeCardPreview() {
    SakeCard(
        sake = Sake(id = "1", name = "獺祭", type = "純米大吟醸"),
        onClick = {}
    )
}
```

### Material 3 の使用

- Material 3 コンポーネントを優先的に使用
- カスタムコンポーネントは Material 3 のスタイルに準拠

```kotlin
import androidx.compose.material3.*

@Composable
fun MyScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("酒一覧") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        // Content
    }
}
```

## 状態管理

### ViewModel から StateFlow で公開

```kotlin
// ViewModel
class SakeListViewModel(
    private val getSakeListUseCase: GetSakeListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SakeListUiState())
    val uiState: StateFlow<SakeListUiState> = _uiState.asStateFlow()
}

// Composable
@Composable
fun SakeListScreen(
    viewModel: SakeListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SakeListContent(
        uiState = uiState,
        onSakeClick = { viewModel.selectSake(it) }
    )
}
```

### collectAsState() で購読

- `collectAsState()` を使用して StateFlow を State に変換
- 再コンポジションのスコープを最小化

```kotlin
// ✅ 良い例 - スコープを最小化
@Composable
fun SakeListScreen(viewModel: SakeListViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // uiStateが変更されたときのみ再コンポジション
    SakeListContent(uiState = uiState)
}

// ❌ 悪い例 - 全体が再コンポジションされる
@Composable
fun SakeListScreen(viewModel: SakeListViewModel) {
    SakeListContent(uiState = viewModel.uiState.value)
}
```

### remember で UI ローカル状態管理

```kotlin
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (FilterCriteria) -> Unit
) {
    var selectedType by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("フィルター") },
        text = {
            Column {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = { selectedType = it },
                    label = { Text("種類") }
                )
                OutlinedTextField(
                    value = selectedRegion,
                    onValueChange = { selectedRegion = it },
                    label = { Text("産地") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onApply(FilterCriteria(selectedType, selectedRegion))
            }) {
                Text("適用")
            }
        }
    )
}
```

## レイアウト

### gap, padding を仕様通りに

デザイン仕様（.penファイル）の gap, padding, width, height を厳密に守ってください。

```kotlin
// デザイン仕様: gap=16dp, padding=24dp
@Composable
fun SakeListContent(sakes: List<Sake>) {
    LazyColumn(
        modifier = Modifier.padding(24.dp), // padding仕様通り
        verticalArrangement = Arrangement.spacedBy(16.dp) // gap仕様通り
    ) {
        items(sakes) { sake ->
            SakeCard(sake = sake)
        }
    }
}
```

### Modifier の順序

Modifier は以下の順序で指定してください:

1. size (width, height, fillMaxWidth など)
2. padding
3. background
4. border
5. clickable

```kotlin
// ✅ 良い例
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
        .background(Color.White)
        .border(1.dp, Color.Gray)
        .clickable { /* action */ }
)

// ❌ 悪い例（順序が不適切）
Box(
    modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()
        .clickable { /* action */ }
        .padding(16.dp)
)
```

## LazyColumn / LazyRow の使用

### パフォーマンス最適化

```kotlin
@Composable
fun SakeList(
    sakes: List<Sake>,
    onSakeClick: (Sake) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = sakes,
            key = { it.id } // key指定でパフォーマンス向上
        ) { sake ->
            SakeCard(
                sake = sake,
                onClick = { onSakeClick(sake) }
            )
        }
    }
}
```

### ページネーション対応

```kotlin
@Composable
fun SakeListWithPagination(
    viewModel: SakeListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        items(uiState.sakes) { sake ->
            SakeCard(sake = sake)
        }

        // 最後のアイテムが表示されたら次ページを読み込み
        if (uiState.hasMore && !uiState.isLoading) {
            item {
                LaunchedEffect(Unit) {
                    viewModel.loadNextPage()
                }
            }
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
```

## デザイン仕様準拠の徹底

### .penファイルの確認

1. `docs/screens/` から該当.penファイルを確認
2. 画面番号、コンポーネントIDを特定
3. gap, padding, width, height を確認
4. デザインシステムの再利用可能コンポーネントを確認

### 仕様に基づいた実装

```kotlin
// デザイン仕様例:
// - gap: 16dp
// - padding: 24dp
// - カードの高さ: 120dp
// - カードの幅: fill_container

@Composable
fun SakeListScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // 仕様通り
        verticalArrangement = Arrangement.spacedBy(16.dp) // 仕様通り
    ) {
        repeat(3) {
            Card(
                modifier = Modifier
                    .fillMaxWidth() // fill_container
                    .height(120.dp) // 仕様通り
            ) {
                // Card content
            }
        }
    }
}
```

## ナビゲーション

### Navigation Compose の使用

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "sake_list"
    ) {
        composable("sake_list") {
            SakeListScreen(
                onSakeClick = { sake ->
                    navController.navigate("sake_detail/${sake.id}")
                }
            )
        }
        composable(
            route = "sake_detail/{sakeId}",
            arguments = listOf(navArgument("sakeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sakeId = backStackEntry.arguments?.getString("sakeId")
            SakeDetailScreen(sakeId = sakeId)
        }
    }
}
```

## エラーハンドリング

### エラー状態の表示

```kotlin
@Composable
fun SakeListContent(uiState: SakeListUiState) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("エラーが発生しました")
                    Text(uiState.error)
                }
            }
        }
        else -> {
            LazyColumn {
                items(uiState.sakes) { sake ->
                    SakeCard(sake = sake)
                }
            }
        }
    }
}
```

## 画像の読み込み

### Coil の使用

```kotlin
import coil3.compose.AsyncImage

@Composable
fun SakeImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(Res.drawable.placeholder),
        error = painterResource(Res.drawable.error)
    )
}
```

## プレビュー

### プレビューの活用

```kotlin
@Preview
@Composable
fun SakeListPreview() {
    MaterialTheme {
        SakeListContent(
            uiState = SakeListUiState(
                sakes = listOf(
                    Sake(id = "1", name = "獺祭", type = "純米大吟醸"),
                    Sake(id = "2", name = "久保田", type = "純米吟醸")
                )
            )
        )
    }
}

@Preview
@Composable
fun SakeListLoadingPreview() {
    MaterialTheme {
        SakeListContent(
            uiState = SakeListUiState(isLoading = true)
        )
    }
}

@Preview
@Composable
fun SakeListErrorPreview() {
    MaterialTheme {
        SakeListContent(
            uiState = SakeListUiState(error = "ネットワークエラー")
        )
    }
}
```
