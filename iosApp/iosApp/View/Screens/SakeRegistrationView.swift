import SwiftUI
import PhotosUI

/// 酒登録画面
/// デザイン仕様: docs/screens/stock_list.pen - 在庫登録ダイアログ
struct SakeRegistrationView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var name = ""
    @State private var phonetic = ""
    @State private var selectedCategory: SakeCategory?
    @State private var subcategory = ""
    @State private var region = ""
    @State private var price = ""
    @State private var alcoholPercentage = ""
    @State private var volumeMax = ""
    @State private var volumeRemain = ""
    @State private var description = ""
    @State private var showingImagePicker = false
    @State private var selectedImage: UIImage?
    @State private var showingCancelAlert = false

    // 登録完了時のコールバック
    private let onDismiss: ((Bool) -> Void)

    // プレビュー用フラグ（デフォルトはfalse）
    private let isPreview: Bool

    init(isPreview: Bool = false, onDismiss: @escaping ((Bool) -> Void) = { _ in }) {
        self.isPreview = isPreview
        self.onDismiss = onDismiss
    }

    private var hasInput: Bool {
        !name.isEmpty ||
        !phonetic.isEmpty ||
        selectedCategory != nil ||
        !subcategory.isEmpty ||
        !region.isEmpty ||
        !price.isEmpty ||
        !alcoholPercentage.isEmpty ||
        !volumeMax.isEmpty ||
        !volumeRemain.isEmpty ||
        !description.isEmpty ||
        selectedImage != nil
    }

    private var isValid: Bool {
        !name.isEmpty && selectedCategory != nil
    }

    var body: some View {
        NavigationStack {
            Form {
                // 画像セクション
                if !isPreview {
                    Section {
                        ImagePickerButton(
                            image: $selectedImage,
                            onTap: { showingImagePicker = true }
                        )
                    }
                }

                // 基本情報
                Section("基本情報") {
                    HStack(spacing: 0) {
                        Text("*").foregroundColor(.red)
                        TextField(" 酒名", text: $name)
                            .textInputAutocapitalization(.never)
                    }

                    TextField("ふりがな", text: $phonetic)
                        .textInputAutocapitalization(.never)

                    HStack(spacing: 0) {
                        Text("*").foregroundColor(.red)
                        Picker(" 大分類", selection: $selectedCategory) {
                            Text("選択してください").tag(nil as SakeCategory?)
                            ForEach(SakeCategory.allCases) { category in
                                Text(category.displayName).tag(category as SakeCategory?)
                            }
                        }
                    }

                    TextField("小分類", text: $subcategory)
                        .textInputAutocapitalization(.never)

                    TextField("産地", text: $region)
                        .textInputAutocapitalization(.never)
                }

                // 詳細情報
                Section("詳細情報") {
                    TextField("価格（円）", text: $price)
                        .keyboardType(.numbersAndPunctuation)

                    TextField("アルコール度数（%）", text: $alcoholPercentage)
                        .keyboardType(.decimalPad)

                    HStack {
                        TextField("購入時容量", text: $volumeMax)
                            .keyboardType(.numberPad)
                        Text("ml")
                    }

                    HStack {
                        TextField("残り容量", text: $volumeRemain)
                            .keyboardType(.numberPad)
                        Text("ml")
                    }
                }

                // メモ
                Section("メモ") {
                    TextField("自由入力メモ", text: $description, axis: .vertical)
                        .lineLimit(3...6)
                }

                // 登録ボタン
                Section {
                    Button(action: registerSake) {
                        Text("登録")
                            .frame(maxWidth: .infinity)
                    }
                    .disabled(!isValid)
                }
            }
            .navigationTitle("酒を登録")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("キャンセル") {
                        if hasInput {
                            showingCancelAlert = true
                        } else {
                            onDismiss(false)
                            dismiss()
                        }
                    }
                }
            }
            .alert("入力中の内容が破棄されます", isPresented: $showingCancelAlert) {
                Button("キャンセル", role: .cancel) { }
                Button("破棄", role: .destructive) {
                    onDismiss(false)
                    dismiss()
                }
            } message: {
                Text("編集中の内容は保存されません。本当に閉じますか？")
            }
            .sheet(isPresented: $showingImagePicker) {
                if !isPreview {
                    ImagePicker(image: $selectedImage)
                }
            }
        }
    }

    private func registerSake() {
        // TODO: KMPのUseCaseを呼び出して登録処理
        print("""
        登録:
        - 酒名: \(name)
        - ふりがな: \(phonetic)
        - 大分類: \(selectedCategory?.displayName ?? "")
        - 小分類: \(subcategory)
        - 産地: \(region)
        - 価格: \(price)
        - アルコール度数: \(alcoholPercentage)%
        - 購入時容量: \(volumeMax)ml
        - 残り容量: \(volumeRemain)ml
        - メモ: \(description)
        """)
        onDismiss(true)
        dismiss()
    }
}

// MARK: - Models

enum SakeCategory: String, CaseIterable, Identifiable {
    case seishu = "seishu"
    case shochu = "shochu"
    case awamori = "awamori"
    case beer = "beer"
    case wine = "wine"
    case fruitWine = "fruit_wine"
    case western = "western"
    case nonAlcohol = "non_alcohol"
    case other = "other"

    var id: String { rawValue }
    var displayName: String {
        switch self {
        case .seishu: return "清酒"
        case .shochu: return "焼酎"
        case .awamori: return "泡盛"
        case .beer: return "ビール"
        case .wine: return "ワイン"
        case .fruitWine: return "果実酒"
        case .western: return "洋酒"
        case .nonAlcohol: return "ノンアルコール"
        case .other: return "その他"
        }
    }
}

// MARK: - Image Picker Components

struct ImagePickerButton: View {
    @Binding var image: UIImage?
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            ZStack {
                if let image = image {
                    Image(uiImage: image)
                        .resizable()
                        .aspectRatio(3/4, contentMode: .fill)
                        .frame(maxWidth: .infinity)
                        .clipped()
                        .cornerRadius(12)
                } else {
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .aspectRatio(3/4, contentMode: .fit)
                        .frame(maxWidth: .infinity)
                        .cornerRadius(12)
                        .overlay(
                            VStack(spacing: 8) {
                                Image(systemName: "camera")
                                    .font(.title2)
                                    .foregroundColor(.gray)
                                Text("写真を追加")
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                        )
                }
            }
        }
        .buttonStyle(.plain)
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    @Environment(\.dismiss) private var dismiss

    func makeUIViewController(context: Context) -> PHPickerViewController {
        var config = PHPickerConfiguration()
        config.filter = .images
        config.selectionLimit = 1

        let picker = PHPickerViewController(configuration: config)
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: PHPickerViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, PHPickerViewControllerDelegate {
        let parent: ImagePicker

        init(_ parent: ImagePicker) {
            self.parent = parent
        }

        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            picker.dismiss(animated: true)

            guard let provider = results.first?.itemProvider else { return }

            if provider.canLoadObject(ofClass: UIImage.self) {
                provider.loadObject(ofClass: UIImage.self) { image, _ in
                    DispatchQueue.main.async {
                        self.parent.image = image as? UIImage
                    }
                }
            }
        }
    }
}
