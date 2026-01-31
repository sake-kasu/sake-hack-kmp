---
paths:
  - "iosApp/**/*.swift"
  - "core/common/src/iosMain/**/*.kt"
---

# iOS MV + KMP Clean Architecture ガイドライン

このプロジェクトは **SwiftUI MV パターン + KMP Clean Architecture** を採用しています。

## アーキテクチャ概要

```
┌────────────────────────────────────────────────────────┐
│  iosApp (SwiftUI) - MV Pattern                         │
│  ┌──────────────┐    ┌─────────────────────────────┐   │
│  │   View       │ ←→ │  Model (@Observable)        │   │
│  │  (SwiftUI)   │    │  - Wraps KMP UseCase/State  │   │
│  └──────────────┘    └─────────────────────────────┘   │
└────────────────────────────────────────────────────────┘
                           ↓ Calls
┌────────────────────────────────────────────────────────┐
│  KMP commonMain - Clean Architecture                   │
│  ┌─────────────────────────────────────────────────┐   │
│  │  Presentation (共有ViewModel / UiState)         │   │
│  └─────────────────────────────────────────────────┘   │
│                          ↓                             │
│  ┌─────────────────────────────────────────────────┐   │
│  │  Domain (UseCase, Repository Interface, Model)  │   │
│  └─────────────────────────────────────────────────┘   │
│                          ↓                             │
│  ┌─────────────────────────────────────────────────┐   │
│  │  Data (Repository Impl, DataSource, Mapper)     │   │
│  └─────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────┘
```

## iOS 側のディレクトリ構造

```
iosApp/
├── iosApp/
│   ├── iOSApp.swift          # アプリエントリーポイント
│   ├── ContentView.swift     # メインView
│   ├── Model/                # MV パターンの Model 層
│   │   ├── BaseModel.swift   # 基底プロトコル
│   │   ├── KMPViewModelConnector.swift  # KMP連携ユーティリティ
│   │   └── SakeListModel.swift          # 機能別Model
│   └── View/                 # SwiftUI View 層
│       └── SakeListView.swift
├── project.yml               # XcodeGen設定
└── Makefile
```

## MV パターンの原則

### Model 層の責務

```swift
@Observable
final class SakeListModel {
    // 公開状態 (View から参照)
    private(set) var state = SakeListState()
    var error: ModelError?

    // KMP ViewModel への参照
    // private let viewModel: SakeListViewModel
    // private var stateObserver: Closeable?

    // ユーザーアクション
    func loadSakes() { ... }
    func openFilterDialog() { ... }
    func applyFilter() { ... }
}
```

**重要**:
- `@Observable` マクロを使用
- 状態は `private(set)` で外部からの直接変更を防止
- KMP ViewModel/UseCase をラップしてビジネスロジックを委譲

### View 層の責務

```swift
struct SakeListView: View {
    @State private var model = SakeListModel()

    var body: some View {
        NavigationStack {
            List(model.state.sakes) { sake in
                SakeRowView(sake: sake)
                    .onTapGesture {
                        model.openSakeDetail(sake)
                    }
            }
        }
    }
}
```

**重要**:
- View は Model を `@State` で保持
- ViewModel は不要 (SwiftUI の `@State` がバインディングを処理)
- ユーザーアクションは Model のメソッドを直接呼び出し

## KMP との連携パターン

### StateFlow の監視

```swift
// Model で KMP StateFlow を監視
private func observeState() {
    stateObserver = viewModel.uiState.observe { [weak self] kmpState in
        self?.handleKMPStateChange(kmpState)
    }
}

// Swift State に変換
private func handleKMPStateChange(_ kmpState: SakeListUiState) {
    state = SakeListState(
        sakes: kmpState.displayedSake.map { $0.toSwift() },
        isInitialLoading: kmpState.isInitialLoading,
        // ...
    )
}
```

### CFlow (StateFlow ラッパー)

KMP 側で提供される `CFlow` を使用:

```kotlin
// KMP commonMain
fun <T> StateFlow<T>.observe(callback: (T) -> Unit): Closeable {
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    callback(value)  // 初期値を通知
    onEach { callback(it) }.launchIn(scope)
    return object : Closeable {
        override fun close() { job.cancel() }
    }
}
```

### Intent の送信

```swift
// Swift から KMP Intent を送信
func loadSakes() {
    viewModel.handleIntent(SakeListIntent.LoadSakeList())
}

func applyFilter() {
    viewModel.handleIntent(SakeListIntent.ApplyFilter())
}
```

## 命名規則

| 要素 | Swift | KMP |
|------|-------|-----|
| 画面状態 | `[Feature]State` | `[Feature]UiState` |
| Model | `[Feature]Model` | `[Feature]ViewModel` |
| View | `[Feature]View` | - |
| ユーザーアクション | メソッド | `[Feature]Intent` |

## 禁止事項

### ❌ View に直接ビジネスロジックを書く

```swift
// ❌ 悪い例
struct SakeListView: View {
    @State private var sakes: [Sake] = []

    var body: some View {
        List(sakes) { ... }
            .onAppear {
                // View 内で直接 API 呼び出し
                Task {
                    sakes = try await api.getSakes()
                }
            }
    }
}
```

### ✅ Model 経由でアクセス

```swift
// ✅ 良い例
struct SakeListView: View {
    @State private var model = SakeListModel()

    var body: some View {
        List(model.state.sakes) { ... }
            .onAppear {
                model.loadSakes()
            }
    }
}
```

### ❌ ViewModel パターンを使う

```swift
// ❌ 不要 - SwiftUI では ViewModel は不要
class SakeListViewModel: ObservableObject {
    @Published var sakes: [Sake] = []
}
```

SwiftUI の `@Observable` + `@State` が ViewModel の役割を果たすため、別途 ViewModel は作成しない。

## エラーハンドリング

### ModelError の使用

```swift
enum ModelError: Error, Identifiable {
    case network(message: String, isRetryable: Bool)
    case http(statusCode: Int, message: String)
    case api(code: String, message: String)
    case parse(message: String)
    case unknown(message: String)
}
```

### View でのエラー表示

```swift
.alert(item: $model.error) { error in
    Alert(
        title: Text("エラー"),
        message: Text(error.localizedDescription),
        dismissButton: .default(Text("OK"), action: model.clearError)
    )
}
```

## 実装チェックリスト

- [ ] Model は `@Observable` マクロを使用
- [ ] 状態は `private(set)` で保護
- [ ] View は Model を `@State` で保持
- [ ] ビジネスロジックは KMP UseCase/ViewModel に委譲
- [ ] エラーは `ModelError` でラップ
- [ ] KMP StateFlow の監視は `observe()` + `Closeable` で管理
- [ ] `deinit` で `Closeable.close()` を呼び出し
