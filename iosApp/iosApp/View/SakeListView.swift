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
            content
                .navigationTitle("酒一覧")
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        filterButton
                    }
                }
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
                .alert(item: $model.error) { error in
                    Alert(
                        title: Text("エラー"),
                        message: Text(error.localizedDescription),
                        dismissButton: .default(Text("OK"), action: model.clearError)
                    )
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

    private var sakeList: some View {
        List {
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
        .listStyle(.plain)
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

    // MARK: - Filter Button

    private var filterButton: some View {
        Button {
            model.openFilterDialog()
        } label: {
            Image(systemName: model.state.filterCriteria.isActive ? "line.3.horizontal.decrease.circle.fill" : "line.3.horizontal.decrease.circle")
        }
    }
}

// MARK: - SakeRowView

/// 酒一覧の行
struct SakeRowView: View {
    let sake: Sake

    var body: some View {
        HStack(spacing: 12) {
            // サムネイル
            sakeImage

            // 情報
            VStack(alignment: .leading, spacing: 4) {
                Text(sake.name)
                    .font(.headline)
                    .lineLimit(1)

                Text(sake.type)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)

                HStack {
                    Text(sake.brewery)
                    Text("・")
                    Text(sake.prefecture)
                }
                .font(.caption)
                .foregroundStyle(.tertiary)

                if let rating = sake.rating {
                    HStack(spacing: 2) {
                        Image(systemName: "star.fill")
                            .foregroundStyle(.yellow)
                        Text(String(format: "%.1f", rating))
                    }
                    .font(.caption)
                }
            }

            Spacer()

            Image(systemName: "chevron.right")
                .foregroundStyle(.tertiary)
        }
        .padding(.vertical, 8)
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
        .frame(width: 60, height: 80)
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
