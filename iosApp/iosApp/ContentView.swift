import SwiftUI
import Greeting

/// メインコンテンツビュー
///
/// MV パターンでは View が Model を直接参照
/// KMP Clean Architecture のビジネスロジックは Model 層経由でアクセス
struct ContentView: View {

    var body: some View {
        TabView {
            // 酒一覧タブ (MV パターン サンプル)
            SakeListView()
                .tabItem {
                    Label("酒一覧", systemImage: "wineglass")
                }

            // KMP連携サンプルタブ
            KMPSampleView()
                .tabItem {
                    Label("KMPサンプル", systemImage: "swift")
                }
        }
    }
}

// MARK: - KMP Sample View

/// KMP 連携サンプルビュー
/// Greeting モジュールとの連携例
struct KMPSampleView: View {

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
                        Text("KMP UseCase からの取得:")
                            .font(.caption)
                            .foregroundStyle(.secondary)

                        Text(GetPlatformInfoUseCase().invoke())
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
