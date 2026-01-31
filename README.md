This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

このプロジェクトでは、iOSプロジェクトファイルの生成に [XcodeGen](https://github.com/yonaskolb/XcodeGen) を使用しています。

#### 初回セットアップ

1. XcodeGen をインストール（Homebrew経由）:
   ```shell
   brew install xcodegen
   ```

2. iOSプロジェクトを生成:
   ```shell
   xcodegen -s iosApp/project.yml
   ```

#### 開発フロー

- Xcodeで開発する場合: `[/iosApp](./iosApp)` ディレクトリをXcodeで開いて実行
- 新しいファイルを追加した場合: プロジェクトルートで `xcodegen -s iosApp/project.yml` を再実行

**注意**: `iosApp.xcodeproj` はXcodeGenで自動生成されるため、手動で編集しないでください。設定を変更する場合は `project.yml` を編集してから再生成してください。

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…