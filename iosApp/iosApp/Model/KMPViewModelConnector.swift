import Foundation
import SwiftUI

// MARK: - KMP ViewModel Connector

/// KMP ViewModel と SwiftUI Model を接続するためのユーティリティ
///
/// 使用例:
/// ```swift
/// @Observable
/// final class SakeListModel {
///     private(set) var sakes: [Sake] = []
///     private(set) var isLoading = false
///
///     private let connector: KMPViewModelConnector<SakeListUiState>
///
///     init(viewModel: SakeListViewModel) {
///         connector = KMPViewModelConnector(
///             stateFlow: viewModel.uiState,
///             onStateChange: { [weak self] state in
///                 self?.handleStateChange(state)
///             }
///         )
///     }
/// }
/// ```

/// KMP StateFlow を監視して SwiftUI Model に変換するコネクタ
final class KMPViewModelConnector<State> {

    private var observer: Closeable?
    private let onStateChange: (State) -> Void

    /// イニシャライザ
    /// - Parameters:
    ///   - stateFlow: KMP の StateFlow (CFlow でラップされたもの)
    ///   - onStateChange: State 変更時のコールバック
    init<T: AnyObject>(
        stateFlow: T,
        initialValue: State,
        onStateChange: @escaping (State) -> Void
    ) {
        self.onStateChange = onStateChange

        // 初期値を即座に反映
        onStateChange(initialValue)

        // Note: 実際の KMP StateFlow との接続は
        // KMP フレームワークがエクスポートされた後に実装
        // 以下はテンプレートコード:
        //
        // if let cflow = stateFlow as? CFlow<State> {
        //     observer = cflow.watch { [weak self] state in
        //         DispatchQueue.main.async {
        //             self?.onStateChange(state)
        //         }
        //     }
        // }
    }

    deinit {
        observer?.close()
    }
}

// MARK: - Environment Key for KMP

/// KMP の DI コンテナへのアクセスを提供する EnvironmentKey
///
/// 使用例:
/// ```swift
/// struct SakeListView: View {
///     @Environment(\.kmpContainer) private var container
///
///     var body: some View {
///         // container からモデルを取得
///     }
/// }
/// ```
private struct KMPContainerKey: EnvironmentKey {
    static let defaultValue: KMPContainer = KMPContainer()
}

extension EnvironmentValues {
    var kmpContainer: KMPContainer {
        get { self[KMPContainerKey.self] }
        set { self[KMPContainerKey.self] = newValue }
    }
}

// MARK: - KMP Container

/// KMP の DI コンテナをラップするクラス
///
/// KMP 連携は今後実装
final class KMPContainer {

    // MARK: - Model Factory Methods

    /// SakeListModel を作成
    /// - Returns: SakeListModel インスタンス
    func makeSakeListModel() -> SakeListModel {
        // 実際の実装では KMP の ViewModel を取得して Model に渡す
        // let viewModel = KoinHelper.getSakeListViewModel()
        // return SakeListModel(viewModel: viewModel)
        return SakeListModel()
    }
}
