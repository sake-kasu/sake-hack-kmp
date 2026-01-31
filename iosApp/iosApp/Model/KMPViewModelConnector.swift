import Foundation
import SwiftUI

// Bridging Header 経由で KMP フレームワークをインポート
// Greeting.framework/Headers/Greeting.h が自動的にインポートされる

// MARK: - KMP Container

/// KMP の DI コンテナをラップするクラス
///
/// 遅延初期化によりアプリ起動時のブロッキングを回避
final class KMPContainer {

    private var _greetingViewModel: GreetingViewModel?

    /// GreetingViewModel（遅延初期化）
    /// Swift では swift_name 属性で指定された名前を使用
    var greetingViewModel: GreetingViewModel {
        if let cached = _greetingViewModel {
            return cached
        }
        let instance = GreetingViewModel(getPlatformInfoUseCase: GetPlatformInfoUseCase())
        _greetingViewModel = instance
        return instance
    }

    // MARK: - Model Factory Methods

    /// SakeListModel を作成
    ///
    /// 注: 簡易版実装のため、現在はSwift単独で動作
    /// 将来的には KMP SakeListViewModel を連携予定
    /// - Returns: SakeListModel インスタンス
    func makeSakeListModel() -> SakeListModel {
        return SakeListModel()
    }
}
