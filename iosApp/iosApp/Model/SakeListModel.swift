import Foundation
import SwiftUI

// MARK: - Sake Domain Model (iOS)

/// 酒のドメインモデル
/// KMP の Sake モデルを Swift 側で表現
struct Sake: Identifiable, Equatable {
    let id: String
    let name: String
    let type: String
    let brewery: String
    let prefecture: String
    let description: String?
    let imageUrl: String?
    let alcoholPercentage: Double?
    let rating: Double?
}

// MARK: - Filter Criteria

/// フィルター条件
struct FilterCriteria: Equatable {
    var sakeName: String?
    var sakeTypes: Set<String>
    var region: String?

    init(sakeName: String? = nil, sakeTypes: Set<String> = [], region: String? = nil) {
        self.sakeName = sakeName
        self.sakeTypes = sakeTypes
        self.region = region
    }

    var isActive: Bool {
        !(sakeName?.isEmpty ?? true) || !sakeTypes.isEmpty || !(region?.isEmpty ?? true)
    }
}

// MARK: - SakeList State

/// 酒一覧画面の状態
struct SakeListState: Equatable {
    var sakes: [Sake] = []
    var isInitialLoading: Bool = false
    var isLoadingMore: Bool = false
    var currentOffset: Int = 0
    var totalCount: Int = 0
    var filterCriteria: FilterCriteria = FilterCriteria()
    var isFilterDialogOpen: Bool = false
    var selectedSake: Sake? = nil
    var isDetailSheetOpen: Bool = false

    var hasNextPage: Bool {
        currentOffset + sakes.count < totalCount
    }
}

// MARK: - SakeList Model

/// 酒一覧画面の Model (MV パターン)
///
/// 責務:
/// - KMP SakeListViewModel との連携
/// - UI 状態の管理
/// - ユーザーアクションの処理
@Observable
final class SakeListModel {

    // MARK: - Published State

    var state = SakeListState()
    var error: ModelError?

    // MARK: - Private Properties

    // KMP ViewModel への参照 (実際の連携時に使用)
    // private let viewModel: SakeListViewModel
    // private var stateObserver: Closeable?

    // MARK: - Initialization

    init() {
        // 実際の実装では KMP ViewModel を受け取る
        // init(viewModel: SakeListViewModel) {
        //     self.viewModel = viewModel
        //     observeState()
        // }

        // サンプルデータをロード
        loadSampleData()
    }

    // MARK: - KMP State Observation

    // private func observeState() {
    //     stateObserver = viewModel.uiState.observe { [weak self] kmpState in
    //         self?.handleKMPStateChange(kmpState)
    //     }
    // }
    //
    // private func handleKMPStateChange(_ kmpState: SakeListUiState) {
    //     state = SakeListState(
    //         sakes: kmpState.displayedSake.map { $0.toSwift() },
    //         isInitialLoading: kmpState.isInitialLoading,
    //         isLoadingMore: kmpState.isLoadingMore,
    //         currentOffset: Int(kmpState.currentOffset),
    //         totalCount: Int(kmpState.totalCount),
    //         filterCriteria: kmpState.filterCriteria.toSwift(),
    //         isFilterDialogOpen: kmpState.isFilterDialogOpen,
    //         selectedSake: kmpState.selectedSake?.toSwift(),
    //         isDetailSheetOpen: kmpState.isDetailDialogOpen
    //     )
    // }

    // MARK: - User Actions (Intent)

    /// 酒一覧を読み込み
    func loadSakes() {
        // viewModel.handleIntent(SakeListIntent.LoadSakeList())
        state.isInitialLoading = true
        // 実際の実装では KMP UseCase を呼び出す
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) { [weak self] in
            self?.loadSampleData()
            self?.state.isInitialLoading = false
        }
    }

    /// 次ページを読み込み
    func loadNextPage() {
        guard state.hasNextPage && !state.isLoadingMore else { return }
        // viewModel.handleIntent(SakeListIntent.LoadNextPage())
        state.isLoadingMore = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            self?.state.isLoadingMore = false
        }
    }

    /// プルリフレッシュ
    func refresh() async {
        // viewModel.handleIntent(SakeListIntent.Refresh())
        state.isInitialLoading = true
        try? await Task.sleep(nanoseconds: 1_000_000_000)
        loadSampleData()
        state.isInitialLoading = false
    }

    /// フィルターダイアログを開く
    func openFilterDialog() {
        // viewModel.handleIntent(SakeListIntent.OpenFilterDialog())
        state.isFilterDialogOpen = true
    }

    /// フィルターダイアログを閉じる
    func closeFilterDialog() {
        // viewModel.handleIntent(SakeListIntent.CloseFilterDialog())
        state.isFilterDialogOpen = false
    }

    /// フィルターを適用
    func applyFilter() {
        // viewModel.handleIntent(SakeListIntent.ApplyFilter())
        state.isFilterDialogOpen = false
        loadSakes()
    }

    /// フィルターをクリア
    func clearFilter() {
        // viewModel.handleIntent(SakeListIntent.ClearFilter())
        state.filterCriteria = FilterCriteria()
        state.isFilterDialogOpen = false
        loadSakes()
    }

    /// 酒の詳細を開く
    func openSakeDetail(_ sake: Sake) {
        // viewModel.handleIntent(SakeListIntent.OpenSakeDetail(sake: sake.toKMP()))
        state.selectedSake = sake
        state.isDetailSheetOpen = true
    }

    /// 酒の詳細を閉じる
    func closeSakeDetail() {
        // viewModel.handleIntent(SakeListIntent.CloseDetailDialog())
        state.selectedSake = nil
        state.isDetailSheetOpen = false
    }

    /// エラーをクリア
    func clearError() {
        error = nil
    }

    // MARK: - Filter Actions

    /// 酒名フィルターを更新
    func updateSakeNameFilter(_ name: String?) {
        state.filterCriteria.sakeName = name
    }

    /// 酒タイプフィルターをトグル
    func toggleSakeTypeFilter(_ type: String) {
        if state.filterCriteria.sakeTypes.contains(type) {
            state.filterCriteria.sakeTypes.remove(type)
        } else {
            state.filterCriteria.sakeTypes.insert(type)
        }
    }

    /// 地域フィルターを更新
    func updateRegionFilter(_ region: String?) {
        state.filterCriteria.region = region
    }

    // MARK: - Sample Data

    private func loadSampleData() {
        state.sakes = [
            Sake(
                id: "1",
                name: "獺祭 純米大吟醸45",
                type: "純米大吟醸",
                brewery: "旭酒造",
                prefecture: "山口県",
                description: "山田錦を45%まで磨き上げた純米大吟醸",
                imageUrl: nil,
                alcoholPercentage: 16.0,
                rating: 4.5
            ),
            Sake(
                id: "2",
                name: "十四代 本丸",
                type: "本醸造",
                brewery: "高木酒造",
                prefecture: "山形県",
                description: "幻の銘酒と呼ばれる十四代の定番",
                imageUrl: nil,
                alcoholPercentage: 15.0,
                rating: 4.8
            ),
            Sake(
                id: "3",
                name: "而今 純米吟醸",
                type: "純米吟醸",
                brewery: "木屋正酒造",
                prefecture: "三重県",
                description: "フレッシュで華やかな香りが特徴",
                imageUrl: nil,
                alcoholPercentage: 16.0,
                rating: 4.6
            ),
            Sake(
                id: "4",
                name: "新政 No.6",
                type: "純米",
                brewery: "新政酒造",
                prefecture: "秋田県",
                description: "6号酵母で醸す革新的な日本酒",
                imageUrl: nil,
                alcoholPercentage: 15.0,
                rating: 4.7
            ),
            Sake(
                id: "5",
                name: "黒龍 大吟醸",
                type: "大吟醸",
                brewery: "黒龍酒造",
                prefecture: "福井県",
                description: "福井を代表する銘醸蔵の大吟醸",
                imageUrl: nil,
                alcoholPercentage: 15.5,
                rating: 4.4
            )
        ]
        state.totalCount = 50
        state.currentOffset = 0
    }

    // MARK: - Cleanup

    deinit {
        // stateObserver?.close()
    }
}
