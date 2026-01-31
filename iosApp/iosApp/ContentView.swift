import SwiftUI

/// メインコンテンツビュー
struct ContentView: View {

    var body: some View {
        TabView {
            // 酒一覧タブ
            SakeListView()
                .tabItem {
                    Label("酒一覧", systemImage: "wineglass")
                }

            // KMP連携プレースホルダー
            PlaceholderView()
                .tabItem {
                    Label("KMPサンプル", systemImage: "swift")
                }
        }
    }
}

// MARK: - Placeholder View

/// KMP 連携プレースホルダー（今後実装）
struct PlaceholderView: View {

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Spacer()

                Image(systemName: "swift")
                    .font(.system(size: 100))
                    .foregroundStyle(.orange)

                Text("KMP 連携")
                    .font(.title2)
                    .fontWeight(.semibold)

                Text("Kotlin Multiplatform との連携を今後実装")
                    .foregroundStyle(.secondary)

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
