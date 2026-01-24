# CLAUDE.md

ユーザーに質問を要求する際は、できる限りAskUserQuestionを使用してください。 必要な情報のみを簡潔に回答してください。 複数の方法や提案がある場合は、まず比較に必要な最小限の情報のみを示してください。選択後に、該当する方法の詳細を説明してください。

このファイルは、Claude Code (claude.ai/code) がこのリポジトリで作業する際のガイダンスを提供します。

## 前提

ユーザーは Kotlin Multiplatform のスペシャリストですが、作業の効率化を図るためにあなたの作業を依頼しています。
Claude も Kotlin Multiplatform のスペシャリストです。修正は KMP と Clean Architecture のベストプラクティスに沿って実行してください。

## 絶対に守るルール

### 対話ルール

Claude がユーザーと対話する場合は必ず日本語で行ってください。思考プロセスにおいて英語の方が都合が良ければ英語で思考して構いません。

### コーディング関連

全ての出力に対して，全角の()の使用を禁じます。半角の()を使用してください。

#### null 安全性の徹底

- `!!` (非 null アサーション) の使用を完全に禁止します
- null チェックは `?.` (セーフコール) や `?:` (Elvis 演算子) を使用してください
- null 許容型の扱いは、`let`、`also`、`run` などのスコープ関数を活用してください

#### プラットフォーム分離の原則

- **expect/actual は最小限に**: DI とインターフェースを優先してください
- **commonMain に最大限のロジックを配置**: ViewModels、UseCases、Repository インターフェースは全て共通コード
- **プラットフォーム固有コードは androidMain/iosMain のみ**: データベースドライバ、HTTP エンジン、Firebase 初期化など

#### Clean Architecture の遵守

このプロジェクトは Clean Architecture + MVI パターンを採用しています：

```
feature/[feature-name]/
├── domain/
│   ├── model/          # Domain entities (純粋な Kotlin データクラス)
│   ├── repository/     # Repository インターフェース (実装は data 層)
│   └── usecase/        # UseCase (ビジネスロジック)
├── data/
│   ├── repository/     # Repository 実装
│   ├── source/         # DataSource (Remote/Local)
│   └── mapper/         # Entity ↔ Domain model 変換
└── presentation/
    ├── [Feature]ViewModel.kt   # MVI の ViewModel
    └── [Feature]UiState.kt     # 単一の不変 State
```

**重要な制約**:
- Domain 層は Data 層や Presentation 層に依存してはいけません
- Repository は必ずインターフェース (domain/) と実装 (data/) を分離
- ViewModel は UseCase を通してのみデータにアクセス

#### 型安全性の徹底

- 明示的な型宣言を優先してください (型推論に頼りすぎない)
- `Any` 型の使用は最小限に、必要な場合は `sealed class` や `sealed interface` を検討
- ジェネリクスを適切に活用してください

#### Coroutine と Flow の使用

- suspend 関数は `Dispatchers.IO` で実行 (共通コードで直接使用可能)
- UI State の管理は `StateFlow` を使用
- `Flow` の変換は `map`、`flatMapLatest`、`combine` などを活用
- `viewModelScope` でライフサイクルに応じたキャンセルを実現

```kotlin
// 良い例
class HomeViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserUseCase()
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}
```

#### データクラスとイミュータビリティ

- データクラスは `data class` を使用し、全てのプロパティを `val` で宣言
- State の更新は `copy()` を使用して新しいインスタンスを生成
- コレクションは `List`、`Set`、`Map` を使用 (mutable 版は避ける)

#### Koin DI の使用

```kotlin
// commonMain - 共通モジュール定義
val featureModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory { GetUserUseCase(get()) }
    viewModel { HomeViewModel(get()) }
}

// expect/actual でプラットフォームモジュールを分離
expect val platformModule: Module

// androidMain
actual val platformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single<HttpClient> { httpClient() }
}

// iosMain
actual val platformModule = module {
    single { DatabaseDriverFactory() }
    single<HttpClient> { httpClient() }
}
```

### コミット

コミットする場合は適切なコミット粒度を維持し、レビューしやすく、問題が発生した際に特定しやすいコミット履歴を作成してください。

#### コミット分割の基準

1. **機能別コミット**: 一つの機能や修正は一つのコミットにまとめる
2. **レイヤー別コミット**: Domain/Data/Presentation の各レイヤーは必要に応じて別コミット
3. **影響範囲別コミット**: 破壊的変更と非破壊的変更は分離

#### 推奨コミット粒度の例

**✅ 良いコミット例**:

- 1 つの機能追加：UseCase + Repository + ViewModel + Test を 1 コミット
- 1 つのバグ修正：問題修正 + 関連テストを 1 コミット
- リファクタリング：特定のクラス/モジュールの構造改善を 1 コミット
- 設定変更：Gradle 設定ファイル更新のみを 1 コミット

**❌ 避けるべきコミット例**:

- 複数の無関係な変更を 1 つのコミットに含める
- 1 つの変更を複数のコミットに分割しすぎる
- WIP(作業中)コミットを残したままにする
- 機能開発に関係ない、ユーザーがプロジェクトに配置しているだけのファイルのコミット

### コミットメッセージフォーマット

以下の形式でコミットメッセージを作成してください。チケット番号は、ブランチ名の `feature/xxxx` の `xxxx` の部分の数値を使用してください。

```
{type}:{emoji}{対象の説明}(チケット番号)

例：
add:✨酒一覧取得UseCaseを追加(#1)
fix:🐛SQLDelightのクエリでnullハンドリングを修正(#22)
update:⚡ViewModelのState管理をMVIパターンに変更(#33)
refactor:♻️Repository実装をClean Architectureに準拠(#44)
docs:📚アーキテクチャガイドラインをCLAUDE.mdに追記(#55)
```

#### コミットメッセージ記述ルール

- **"Generated with Claude Code" などの自動生成メッセージは含めない**
- **"Co-Authored-By: Claude" などのクレジット表記は含めない**
- コミットメッセージは純粋に変更内容のみを記述する
- 変更の「理由」や「影響」を明確に記述する
- チケット番号には、ブランチ名の `feature/xxxxx` の `xxxxx` の部分の値を使用する

**❌ 避けるべきコミットメッセージ例**:

```
fix:🐛 CSV ImportのUPSERT処理を修正(#1)

deal_codeによる既存レコード検索でUPSERTを実装

🤖 Generated with [Claude Code](https://claude.com/claude-code)
Co-Authored-By: Claude <noreply@anthropic.com>
```

**✅ 正しいコミットメッセージ例**:

```
fix:🐛 SQLDelightクエリのnull安全性を改善(#1)

Repository実装でのデータベースクエリを修正：
- 修正前: !! 演算子による強制アンラップでクラッシュリスク
- 修正後: セーフコールとElvis演算子でnull安全に処理
- エッジケースでのNPE問題を解消
```

#### Type 一覧

- `add`: 新機能の追加
- `fix`: バグ修正
- `update`: 既存機能の改善・拡張
- `refactor`: リファクタリング(機能変更なし)
- `docs`: ドキュメントの追加・更新
- `test`: テストの追加・修正
- `style`: コードフォーマット、コメント修正
- `chore`: ビルド、依存関係、ツール関連
- `remove`: ファイルや機能の削除

#### Emoji 一覧

- 📝 ドキュメント関連
- 🐛 バグ修正
- ⚡ パフォーマンス・機能改善
- ♻️ リファクタリング
- 📚 ドキュメント
- 🧪 テスト
- 💄 UI・スタイル
- 🎨 フォーマット
- 🔧 設定・ツール
- 💚 CI/CD
- ➕ 依存関係追加
- ➖ 依存関係削除
- 🔨 開発用スクリプト
- ✏️ タイポ
- 🌐 文字列リソース
- 🚚 リソースの移動・改名
- 🔊 ログの追加
- 🔇 ログの削除
- 🚸 UX
- 📱 レスポンシブデザイン
- 🥅 エラーハンドリング
- 🔥 削除
- 🗑️ Deprecated のコードの削除
- 🩺 ヘルスチェック
- 🧑‍💻 開発体験向上
- 🦺 バリデーション
- ✨ 新機能
- 🔒 セキュリティ
- 🚨 コンパイラ・リント
- ✅ テスト

#### コミット前のコード品質の確認

**重要**: Claude はビルドコマンドを直接実行しません。コミットを提案する前に、開発者にビルドを依頼してください。

コミット提案時のフロー:
1. コード変更を完了する
2. 開発者に「Android Studioでビルドを確認してください」と依頼する
3. ビルド成功後、コミットを提案する

開発者が確認すべき項目:
- Android Studioでのフルビルド(全モジュールのコンパイルとテスト)
- `./gradlew detekt` - 静的解析(存在する場合)
- `./gradlew ktlintCheck` - Kotlin リンター(存在する場合)

#### コミットタイミング

以下のタイミングでコミットを提案してください

1. **単一機能完了時**: UseCase、Repository、ViewModel の実装とテストが完了した時点
2. **設定変更完了時**: Gradle 設定ファイル、依存関係更新が完了した時点
3. **ドキュメント更新完了時**: ドキュメント追加・更新が完了した時点
4. **バグ修正完了時**: 問題修正とテストが完了した時点
5. **リファクタリング完了時**: Clean Architecture への移行など、コード改善が完了した時点

## 開発コマンド

**重要**: ビルドとアプリ実行は開発者が Android Studio/Xcode で行います。Claude は以下のコマンドを直接実行しません。

参考コマンド(開発者向け):
- `./gradlew build` - フルビルド(全モジュールのコンパイル + テスト実行)
- `./gradlew clean` - ビルド成果物のクリーンアップ
- `./gradlew :composeApp:run` - Compose Multiplatform アプリの実行(デスクトップ)
- `./gradlew :androidApp:installDebug` - Android アプリのビルドとインストール

Claude のルール:
- **ビルド**: 開発者に Android Studio でのビルドを依頼する(Gradle コマンドより高速)
- **Android アプリ実行**: 開発者に Android Studio でのビルド/実行を依頼する
- **iOS アプリ実行**: 開発者に Xcode での実行を依頼する

## アーキテクチャ概要

このプロジェクトは Kotlin Multiplatform (KMP) をベースに、Clean Architecture + MVI パターンで構築されています。

### プロジェクト構造

```
sake-hack-kmp/
├── gradle/libs.versions.toml        # 依存関係の一元管理
├── core/                            # コアインフラモジュール
│   ├── common/                      # 共通ユーティリティ
│   ├── network/                     # Ktor クライアント設定
│   │   ├── src/commonMain/          # API インターフェース
│   │   ├── src/androidMain/         # OkHttp エンジン
│   │   └── src/iosMain/             # Darwin エンジン
│   └── database/                    # SQLDelight 設定
│       ├── src/commonMain/          # クエリ定義、DAO
│       ├── src/androidMain/         # Android ドライバ
│       └── src/iosMain/             # Native ドライバ
│
├── feature/                         # フィーチャーモジュール
│   ├── sakelist/
│   │   └── src/commonMain/kotlin/
│   │       ├── domain/
│   │       │   ├── model/           # Sake.kt などのドメインエンティティ
│   │       │   ├── repository/      # SakeRepository インターフェース
│   │       │   └── usecase/         # GetSakeListUseCase など
│   │       ├── data/
│   │       │   ├── repository/      # SakeRepositoryImpl
│   │       │   └── source/          # Remote/Local データソース
│   │       └── presentation/
│   │           ├── SakeListViewModel.kt
│   │           └── SakeListUiState.kt
│   └── greeting/
│
├── composeApp/                      # Compose Multiplatform UI
│   ├── src/commonMain/              # 共通 UI コンポーネント
│   ├── src/androidMain/
│   └── src/iosMain/
│
├── androidApp/                      # Android エントリーポイント
└── iosApp/                          # iOS Xcode プロジェクト
```

### 技術スタック

- **言語**: Kotlin 2.2.21
- **UI**: Compose Multiplatform 1.9.3
- **ネットワーク**: Ktor 2.3.11 (OkHttp for Android, Darwin for iOS)
- **データベース**: SQLDelight 2.0.2
- **DI**: Koin 4.2.0
- **画像**: Coil 3.0.4
- **ナビゲーション**: Navigation Compose 2.8.6
- **シリアライズ**: kotlinx-serialization 1.7.3

### MVI パターン

このプロジェクトは MVI (Model-View-Intent) パターンを採用しています：

```kotlin
// 単一の不変 State
data class SakeListUiState(
    val sakes: List<Sake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ViewModel
class SakeListViewModel(
    private val getSakeListUseCase: GetSakeListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SakeListUiState())
    val uiState: StateFlow<SakeListUiState> = _uiState.asStateFlow()

    fun loadSakes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSakeListUseCase()
                .onSuccess { sakes ->
                    _uiState.update { it.copy(sakes = sakes, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}
```

### Clean Architecture の各レイヤー

#### Domain Layer (commonMain)
- **責務**: ビジネスロジック、エンティティ定義、Repository インターフェース
- **依存**: なし(最も内側のレイヤー)
- **配置**: `feature/[name]/domain/`

#### Data Layer (commonMain + platform source sets)
- **責務**: Repository 実装、データソース、データマッピング
- **依存**: Domain Layer のみ
- **配置**: `feature/[name]/data/`

#### Presentation Layer (commonMain)
- **責務**: ViewModel、UI State、UI ロジック
- **依存**: Domain Layer のみ(UseCase 経由)
- **配置**: `feature/[name]/presentation/`

#### UI Layer (platform specific)
- **責務**: Compose UI、SwiftUI、プラットフォーム固有の UI
- **依存**: Presentation Layer (ViewModel と State を観察)
- **配置**: `composeApp/src/[platform]Main/`

### プラットフォーム分離戦略

expect/actual は最小限に抑え、以下の場合のみ使用：
- データベースドライバ生成
- HTTP エンジン設定
- プラットフォーム固有のユーティリティ

それ以外は Koin の DI モジュールで抽象化します。

## デザイン仕様の優先順位

### .penファイルは最優先

**重要**: デザイン仕様(.penファイル)は最優先事項です。

- **配置場所**: `docs/screens/` 配下
- **確認項目**: 画面番号、コンポーネントID、レイアウト仕様(gap, padding, width, height)
- **デザインシステム**: 再利用可能なコンポーネントIDを確認

### API仕様とデザイン仕様の不一致時

API仕様書がデザインの要求を満たせない場合:
1. **ユーザーに判断を委ねる** - AskUserQuestionを使用
2. **API都合でUIを勝手に改変しない**
3. デザイン要求を満たすためのAPI変更を提案

例: デザインが「酒名・種類・産地」のフィルターを要求しているが、APIが「typeId・breweryId」しか対応していない場合
→ ユーザーに確認し、必要ならバックエンド側のAPI変更を依頼

## 実装の推奨順序

Clean Architectureに従い、内側のレイヤーから外側へ実装:

1. **Domain層**: Model → Repository インターフェース → UseCase
2. **Presentation層**: Intent → UiState → ViewModel
3. **Data層**: DTO → ApiService → DataSource → Repository実装
4. **UI層**: Composable関数 → デザイン仕様準拠確認

**理由**: 依存関係の方向(外側 → 内側)に逆らわないため

## バックエンドAPI連携

### フィルター・検索パラメータ変更時の注意

Data層でAPIパラメータを変更する場合、**バックエンド側の対応が必須**です。

**変更フロー**:
1. Domain層でFilterCriteriaを変更
2. Data層でAPIパラメータをマッピング
3. **バックエンド側にパラメータ追加を依頼**
4. バックエンド対応完了後にテスト

**重要**: バックエンド未対応の状態でリリースしないこと

## 変更影響範囲の確認

データ構造やインターフェースを変更する場合、影響範囲を確認:

- **ドメインモデル変更時**: Repository インターフェース/実装、UseCase、ViewModel、UI
- **API パラメータ変更時**: ApiService、Repository実装、バックエンド側
- **Intent 変更時**: ViewModel の Intent ハンドラー、UI の Intent 発行箇所

## コミット前チェックリスト

- [ ] `!!` 演算子未使用、全角括弧 `（）` 未使用
- [ ] null安全性確保(`?.`, `?:`, `let` 等)、イミュータブル(`val`, `copy()`, `List`)
- [ ] Domain層が他レイヤーに非依存、ViewModel が UseCase 経由でアクセス
- [ ] .penファイル仕様準拠(gap, padding, width, height)
- [ ] Android Studio ビルド成功、コンパイルエラーなし
- [ ] APIパラメータ変更時、バックエンド側対応確認済み

## 既存実装パターン

新規機能実装時の参考:
- **フィーチャーモジュール**: `feature/sakelist/`
- **MVI ViewModel**: `SakeListViewModel.kt`
- **フィルターダイアログ**: `FilterDialog.kt`
- **ページネーション**: `SakeListViewModel.loadNextPage()`

**命名規則**:
- Intent: `[Feature]Intent`
- UiState: `[Feature]UiState`
- ViewModel: `[Feature]ViewModel`
- Repository: `[Entity]Repository`
- UseCase: `[Action][Entity]UseCase`

## 参考資料

詳細なアーキテクチャガイドラインは `architecture.md` を参照してください：
- Clean Architecture + MVI の詳細
- 本番環境での事例 (Cash App, Netflix, McDonald's など)
- SQLDelight と Ktor の設定パターン
- expect/actual の最小化戦略
