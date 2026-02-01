import SwiftUI

// MARK: - SakeListView

/// 酒一覧画面 (MV パターン)
///
/// Model を直接参照し、状態変更を自動的に反映
/// ViewModelは不要、SwiftUIの@Stateがバインディングを処理
struct SakeListView: View {

    // MV パターン: View は Model を直接参照
    @State private var model = SakeListModel()

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // カスタムTopAppBar - デザイン仕様: height 64pt, padding [0,16], gap 16pt
                customTopAppBar

                // ActionBar - デザイン仕様: gap 12pt, padding [8,12]
                actionBar

                // メインコンテンツ
                content
            }
            .navigationBarHidden(true)
            .refreshable {
                await model.refresh()
            }
                .sheet(isPresented: $model.state.isFilterDialogOpen) {
                    FilterSheet(model: model)
                }
                .sheet(isPresented: $model.state.isDetailSheetOpen) {
                    if let sake = model.state.selectedSake {
                        SakeDetailSheet(sake: sake, onDismiss: model.closeSakeDetail)
                    }
                }
                .sheet(isPresented: $model.state.isRegistrationSheetOpen) {
                    SakeRegistrationView { registered in
                        model.closeRegistrationView()
                    }
                }
                .alert(item: $model.error) { error in
                    Alert(
                        title: Text("エラー"),
                        message: Text(error.localizedDescription),
                        dismissButton: .default(Text("OK"), action: model.clearError)
                    )
                }
                .sheet(isPresented: $model.state.isSortMenuOpen) {
                    SortSheet(model: model)
                }
        }
    }

    // MARK: - Content

    @ViewBuilder
    private var content: some View {
        if model.state.isInitialLoading {
            loadingView
        } else if model.state.sakes.isEmpty {
            emptyView
        } else {
            sakeList
        }
    }

    // MARK: - Loading View

    private var loadingView: some View {
        VStack(spacing: 16) {
            ProgressView()
                .scaleEffect(1.5)
            Text("読み込み中...")
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    // MARK: - Empty View

    private var emptyView: some View {
        ContentUnavailableView(
            "酒が見つかりません",
            systemImage: "wineglass",
            description: Text("フィルターを変更してお試しください")
        )
    }

    // MARK: - Sake List

    /// 酒一覧
    /// デザイン仕様: 背景色 $sumi-50 (#F8F8FB), padding [12,16,16,16], gap 12pt
    private var sakeList: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(model.state.sakes) { sake in
                    SakeRowView(sake: sake)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            model.openSakeDetail(sake)
                        }
                }

                // ページネーション
                if model.state.hasNextPage {
                    loadMoreRow
                }
            }
            .padding(.top, 12)
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        .background(Color(red: 0.97, green: 0.97, blue: 0.98))
        .overlay(alignment: .bottomTrailing) {
            addButton
        }
    }

    // MARK: - Load More Row

    private var loadMoreRow: some View {
        HStack {
            Spacer()
            if model.state.isLoadingMore {
                ProgressView()
            } else {
                Button("もっと見る") {
                    model.loadNextPage()
                }
            }
            Spacer()
        }
        .padding()
        .onAppear {
            model.loadNextPage()
        }
    }

    // MARK: - Custom TopAppBar

    /// カスタムTopAppBar
    /// デザイン仕様: height 64pt, padding [0,16], gap 16pt, 下線 thickness 1
    private var customTopAppBar: some View {
        VStack(spacing: 0) {
            HStack(spacing: 16) {
                Image(systemName: "line.horizontal.3")
                    .font(.system(size: 24))
                    .foregroundStyle(.primary)

                Text("酒一覧")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundStyle(.primary)

                Spacer()
            }
            .padding(.horizontal, 16)
            .frame(height: 64)
            .background(Color(UIColor.systemBackground))

            Divider()
                .frame(height: 1)
        }
    }

    // MARK: - ActionBar

    /// ActionBar - フィルター・並べ替えボタン
    /// デザイン仕様: gap 12pt, padding [8,12], cornerRadius 8pt
    private var actionBar: some View {
        HStack(spacing: 12) {
            // フィルターボタン
            Button {
                model.openFilterDialog()
            } label: {
                HStack(spacing: 6) {
                    Image(systemName: "line.3.horizontal.decrease.circle")
                        .font(.system(size: 20))
                    Text("フィルター")
                        .font(.system(size: 14, weight: .medium))

                    if model.state.filterCriteria.isActive {
                        Text("(\(model.state.filterCriteria.activeCount))")
                            .font(.system(size: 14, weight: .medium))
                    }
                }
                .padding(.vertical, 8)
                .padding(.horizontal, 12)
                .foregroundStyle(.primary)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color(UIColor.separator), lineWidth: 1)
                )
            }

            // 並べ替えボタン
            Button {
                model.toggleSortMenu()
            } label: {
                HStack(spacing: 6) {
                    Image(systemName: "arrow.up.arrow.down")
                        .font(.system(size: 20))
                    Text("並べ替え")
                        .font(.system(size: 14, weight: .medium))
                }
                .padding(.vertical, 8)
                .padding(.horizontal, 12)
                .foregroundStyle(.primary)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color(UIColor.separator), lineWidth: 1)
                )
            }

            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color(UIColor.systemBackground))
    }

    // MARK: - Add Button

    private var addButton: some View {
        Button {
            model.openRegistrationView()
        } label: {
            Image(systemName: "plus")
                .font(.title2)
                .fontWeight(.semibold)
                .foregroundStyle(.white)
                .frame(width: 56, height: 56)
                .background(Color.blue)
                .clipShape(Circle())
                .shadow(radius: 4)
        }
        .padding(.trailing, 16)
        .padding(.bottom, 16)
    }
}

// MARK: - SakeRowView

/// 酒カード
/// デザイン仕様: cornerRadius 12pt, padding 12pt, gap 12pt, shadow blur 8 offset(0,2)
struct SakeRowView: View {
    let sake: Sake

    var body: some View {
        HStack(spacing: 12) {
            // サムネイル - デザイン仕様: 64x64pt, cornerRadius 8pt
            sakeImage

            // 情報 - デザイン仕様: gap 6pt
            VStack(alignment: .leading, spacing: 6) {
                // タイトル - デザイン仕様: fontSize 16, fontWeight 700, lineHeight 1.4
                Text(sake.name)
                    .font(.system(size: 16, weight: .bold))
                    .lineLimit(2)

                // TypeChip + 蔵元名
                HStack(spacing: 8) {
                    // TypeChip - デザイン仕様: cornerRadius 4pt, padding [4,12], fontSize 12
                    Text(sake.type)
                        .font(.system(size: 12))
                        .padding(.vertical, 4)
                        .padding(.horizontal, 12)
                        .background(Color(UIColor.systemGray5))
                        .cornerRadius(4)

                    Text(sake.brewery)
                        .font(.system(size: 12))
                        .foregroundStyle(.secondary)
                        .lineLimit(1)
                }

                // 評価
                if let rating = sake.rating {
                    HStack(spacing: 2) {
                        Image(systemName: "star.fill")
                            .foregroundStyle(.yellow)
                            .font(.system(size: 12))
                        Text(String(format: "%.1f", rating))
                            .font(.system(size: 12))
                    }
                }
            }

            Spacer()
        }
        .padding(12)
        .background(Color(UIColor.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }

    private var sakeImage: some View {
        Group {
            if let imageUrl = sake.imageUrl, let url = URL(string: imageUrl) {
                AsyncImage(url: url) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    placeholderImage
                }
            } else {
                placeholderImage
            }
        }
        .frame(width: 64, height: 64)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }

    private var placeholderImage: some View {
        Rectangle()
            .fill(Color.gray.opacity(0.2))
            .overlay {
                Image(systemName: "wineglass")
                    .foregroundStyle(.gray)
            }
    }
}

// MARK: - FilterSheet

/// フィルターシート
struct FilterSheet: View {
    @Bindable var model: SakeListModel
    @Environment(\.dismiss) private var dismiss

    private let sakeTypes = ["純米大吟醸", "純米吟醸", "純米", "大吟醸", "吟醸", "本醸造"]

    var body: some View {
        NavigationStack {
            Form {
                Section("酒名") {
                    TextField("酒名で検索", text: Binding(
                        get: { model.state.filterCriteria.sakeName ?? "" },
                        set: { model.updateSakeNameFilter($0.isEmpty ? nil : $0) }
                    ))
                }

                Section("種類") {
                    ForEach(sakeTypes, id: \.self) { type in
                        Toggle(type, isOn: Binding(
                            get: { model.state.filterCriteria.sakeTypes.contains(type) },
                            set: { _ in model.toggleSakeTypeFilter(type) }
                        ))
                    }
                }

                Section("地域") {
                    TextField("都道府県", text: Binding(
                        get: { model.state.filterCriteria.region ?? "" },
                        set: { model.updateRegionFilter($0.isEmpty ? nil : $0) }
                    ))
                }
            }
            .navigationTitle("フィルター")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("キャンセル") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .destructiveAction) {
                    Button("クリア") {
                        model.clearFilter()
                    }
                    .foregroundStyle(.red)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("適用") {
                        model.applyFilter()
                    }
                }
            }
        }
        .presentationDetents([.medium, .large])
    }
}

// MARK: - SortSheet

/// 並べ替えシート
struct SortSheet: View {
    @Bindable var model: SakeListModel
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            List {
                ForEach(SortOption.allCases, id: \.self) { option in
                    sortOptionRow(option: option)
                }
            }
            .navigationTitle("並べ替え")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("キャンセル") {
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }

    private func sortOptionRow(option: SortOption) -> some View {
        Button {
            model.applySort(option)
        } label: {
            HStack {
                Text(option.rawValue)
                    .foregroundStyle(.primary)

                Spacer()

                if model.state.sortOption == option {
                    Image(systemName: "checkmark")
                        .foregroundStyle(.blue)
                        .fontWeight(.semibold)
                }
            }
        }
    }
}

// MARK: - SakeDetailSheet

/// 酒詳細シート
struct SakeDetailSheet: View {
    let sake: Sake
    let onDismiss: () -> Void

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // ヘッダー画像
                    headerImage

                    VStack(alignment: .leading, spacing: 12) {
                        // タイトル
                        Text(sake.name)
                            .font(.title2)
                            .fontWeight(.bold)

                        // 種類
                        HStack {
                            Label(sake.type, systemImage: "tag")
                            Spacer()
                            if let rating = sake.rating {
                                HStack(spacing: 4) {
                                    Image(systemName: "star.fill")
                                        .foregroundStyle(.yellow)
                                    Text(String(format: "%.1f", rating))
                                        .fontWeight(.semibold)
                                }
                            }
                        }
                        .foregroundStyle(.secondary)

                        Divider()

                        // 蔵元情報
                        VStack(alignment: .leading, spacing: 8) {
                            Label(sake.brewery, systemImage: "building.2")
                            Label(sake.prefecture, systemImage: "mappin")
                            if let alcohol = sake.alcoholPercentage {
                                Label("\(String(format: "%.1f", alcohol))%", systemImage: "drop")
                            }
                        }
                        .font(.subheadline)

                        // 説明
                        if let description = sake.description {
                            Divider()
                            Text(description)
                                .font(.body)
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle("詳細")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("閉じる") {
                        onDismiss()
                    }
                }
            }
        }
    }

    private var headerImage: some View {
        Group {
            if let imageUrl = sake.imageUrl, let url = URL(string: imageUrl) {
                AsyncImage(url: url) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                } placeholder: {
                    placeholderHeader
                }
            } else {
                placeholderHeader
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 200)
        .background(Color.gray.opacity(0.1))
    }

    private var placeholderHeader: some View {
        VStack {
            Image(systemName: "wineglass")
                .font(.system(size: 60))
                .foregroundStyle(.gray)
        }
    }
}

// MARK: - Preview

#Preview {
    SakeListView()
        .previewInterfaceOrientation(.portrait)
}
