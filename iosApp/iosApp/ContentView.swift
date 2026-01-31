import SwiftUI

/// メインコンテンツビュー
///
/// MV パターンでは View が Model を直接参照
/// KMP Clean Architecture のビジネスロジックは Model 層経由でアクセス
struct ContentView: View {

    @State private var kmpContainer = KMPContainer()

    var body: some View {
        TabView {
            // 酒一覧タブ (MV パターン サンプル)
            SakeListView()
                .tabItem {
                    Label("酒一覧", systemImage: "wineglass")
                }

            // KMP連携サンプルタブ
            KMPSampleView(container: kmpContainer)
                .tabItem {
                    Label("KMPサンプル", systemImage: "swift")
                }
        }
    }
}

// MARK: - KMP Sample View

/// KMP 連携サンプルビュー（遅延初期化）
struct KMPSampleView: View {

    let container: KMPContainer
    @State private var showContent = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Spacer()

                Image(systemName: "swift")
                    .font(.system(size: 100))
                    .foregroundStyle(.orange)

                Text("KMP Clean Architecture")
                    .font(.title2)
                    .fontWeight(.semibold)

                Text("SwiftUI MV + Kotlin Multiplatform")
                    .foregroundStyle(.secondary)

                Button("プラットフォーム情報を表示") {
                    withAnimation {
                        showContent.toggle()
                    }
                }
                .buttonStyle(.borderedProminent)

                if showContent {
                    VStack(spacing: 12) {
                        Text("KMP ViewModel からの取得:")
                            .font(.caption)
                            .foregroundStyle(.secondary)

                        // 遅延初期化：初回アクセス時に KMP モジュールを初期化
                        Text(container.greetingViewModel.greet())
                            .font(.headline)
                            .padding()
                            .background(Color.blue.opacity(0.1))
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }

                Spacer()
            }
            .padding()
            .navigationTitle("KMP サンプル")
        }
    }
}

// MARK: - Preview

#Preview {
    ContentView()
}
