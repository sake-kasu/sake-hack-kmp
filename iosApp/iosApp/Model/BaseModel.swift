import Foundation
import Combine

// MARK: - Base Model Protocol

/// SwiftUI MV パターンの Model 層基底プロトコル
///
/// KMP Clean Architecture と連携するための基底設計:
/// - @Observable マクロを使用した状態管理
/// - KMP UseCase/ViewModel との連携パターン
/// - Side Effect (一回限りイベント) のハンドリング
///
/// 使用例:
/// ```swift
/// @Observable
/// final class SakeListModel: BaseModelProtocol {
///     var state = SakeListState()
///     var isLoading = false
///     var error: ModelError?
///
///     private let viewModel: SakeListViewModel
///     private var stateObserver: Closeable?
///
///     init(viewModel: SakeListViewModel) {
///         self.viewModel = viewModel
///         observeState()
///     }
/// }
/// ```
protocol BaseModelProtocol: AnyObject {
    associatedtype State

    var state: State { get set }
    var isLoading: Bool { get set }
    var error: ModelError? { get set }
}

// MARK: - Model Error

/// Model 層で扱うエラー型
/// KMP の AppError を Swift 側でラップ
enum ModelError: Error, Identifiable {
    case network(message: String, isRetryable: Bool)
    case http(statusCode: Int, message: String)
    case api(code: String, message: String)
    case parse(message: String)
    case unknown(message: String)

    var id: String {
        switch self {
        case .network(let message, _): return "network_\(message)"
        case .http(let code, _): return "http_\(code)"
        case .api(let code, _): return "api_\(code)"
        case .parse(let message): return "parse_\(message)"
        case .unknown(let message): return "unknown_\(message)"
        }
    }

    var localizedDescription: String {
        switch self {
        case .network(let message, _):
            return "ネットワークエラー: \(message)"
        case .http(_, let message):
            return "サーバーエラー: \(message)"
        case .api(_, let message):
            return message
        case .parse(let message):
            return "データ処理エラー: \(message)"
        case .unknown(let message):
            return "予期しないエラー: \(message)"
        }
    }

    var isRetryable: Bool {
        switch self {
        case .network(_, let isRetryable): return isRetryable
        case .http(let code, _): return code >= 500
        case .api: return false
        case .parse: return false
        case .unknown: return true
        }
    }
}

// MARK: - Closeable Protocol

/// KMP の Closeable と同等のプロトコル
/// Flow 監視のキャンセル用
protocol Closeable {
    func close()
}

// MARK: - Observable Extensions

/// @Observable クラスで使用するヘルパー
extension BaseModelProtocol {

    /// エラーをクリア
    func clearError() {
        error = nil
    }

    /// ローディング状態を設定
    func setLoading(_ loading: Bool) {
        isLoading = loading
    }
}
