# KMP architecture: Separating platform code from shared logic

Kotlin Multiplatform's greatest architectural challenge is cleanly separating platform-dependent code from shared business logic—and **2024-2025 best practices have converged on a clear solution**: Clean Architecture with MVI pattern, using expect/actual minimally while leveraging dependency injection for platform abstraction. Companies like Netflix, Cash App, and McDonald's have validated this approach in production apps serving millions of users.

This guide provides production-ready patterns for mobile apps with Firebase integration, local databases, and clean architecture principles based on the latest Kotlin 2.0+ ecosystem.

## The modern KMP architecture stack

The recommended architecture places **business logic and ViewModels in shared code** while keeping UI implementation platform-specific. Here's the layer separation that production apps follow:

```
┌─────────────────────────────────────────────────────────────┐
│                     PLATFORM UI LAYER                        │
│  (Android: Jetpack Compose, iOS: SwiftUI/UIKit)             │
├─────────────────────────────────────────────────────────────┤
│                   PRESENTATION LAYER                         │
│  (Shared ViewModels, UI States, MVI pattern)                │
│                    ↓ commonMain ↓                           │
├─────────────────────────────────────────────────────────────┤
│                     DOMAIN LAYER                             │
│  (Use Cases, Repository Interfaces, Entities)               │
│                    ↓ commonMain ↓                           │
├─────────────────────────────────────────────────────────────┤
│                      DATA LAYER                              │
│  (Repository Implementations, Data Sources, Mappers)        │
│              ↓ commonMain + platform source sets ↓          │
├─────────────────────────────────────────────────────────────┤
│                  PLATFORM SPECIFICS                          │
│  (DB drivers, HTTP engines, Firebase init, DI modules)      │
│               ↓ androidMain / iosMain ↓                     │
└─────────────────────────────────────────────────────────────┘
```

**MVI (Model-View-Intent)** has emerged as the preferred pattern over traditional MVVM for KMP projects because its single immutable state per screen works seamlessly with both Compose and SwiftUI's declarative nature:

```kotlin
// commonMain - Shared ViewModel with single state
data class HomeUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val getArticlesUseCase: GetArticlesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getArticlesUseCase()
                .onSuccess { articles -> _uiState.update { it.copy(articles = articles, isLoading = false) } }
                .onFailure { error -> _uiState.update { it.copy(error = error.message, isLoading = false) } }
        }
    }
}
```

## Directory structure for production projects

The **multi-module, feature-based structure** scales best for production apps. Start with a single shared module for smaller projects, then split by feature as complexity grows:

```
project-root/
├── gradle/libs.versions.toml        # Centralized version catalog
├── build-logic/                     # Convention plugins
│   └── convention/
│
├── core/                            # Core infrastructure modules
│   ├── network/
│   │   ├── src/commonMain/kotlin/   # Ktor client setup, API interfaces
│   │   ├── src/androidMain/kotlin/  # OkHttp engine configuration
│   │   └── src/iosMain/kotlin/      # Darwin engine configuration
│   ├── database/
│   │   ├── src/commonMain/kotlin/   # SQLDelight queries, DAOs
│   │   ├── src/androidMain/kotlin/  # Android driver factory
│   │   └── src/iosMain/kotlin/      # Native driver factory
│   └── common/                      # Shared utilities, extensions
│
├── feature/                         # Feature modules with Clean Architecture
│   ├── home/
│   │   └── src/commonMain/kotlin/com/example/feature/home/
│   │       ├── domain/
│   │       │   ├── model/User.kt              # Domain entities
│   │       │   ├── repository/UserRepository.kt  # Interface only
│   │       │   └── usecase/GetUserUseCase.kt
│   │       ├── data/
│   │       │   ├── repository/UserRepositoryImpl.kt
│   │       │   └── source/UserRemoteDataSource.kt
│   │       └── presentation/
│   │           ├── HomeViewModel.kt
│   │           └── HomeUiState.kt
│   └── profile/
│
├── composeApp/                      # Optional: Compose Multiplatform UI
│   ├── src/commonMain/              # Shared UI components
│   ├── src/androidMain/
│   └── src/iosMain/
│
├── androidApp/                      # Android entry point
└── iosApp/                          # iOS Xcode project
```

**Expect/actual declarations** should be minimal—prefer interfaces with DI injection. When you must use them, organize by platform package:

```kotlin
// commonMain/kotlin/com/example/platform/HttpClientProvider.kt
expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

// androidMain/kotlin/com/example/platform/HttpClientProvider.android.kt
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    config(this)
    engine { config { connectTimeout(30, TimeUnit.SECONDS) } }
}

// iosMain/kotlin/com/example/platform/HttpClientProvider.ios.kt  
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    config(this)
    engine { configureRequest { setAllowsCellularAccess(true) } }
}
```

## Handling OS-dependent components

### Database: SQLDelight or Room

**SQLDelight 2.0.2** remains the mature choice, while **Room 2.7.0+** now supports KMP (still in alpha). For new projects with Firebase and clean architecture, SQLDelight provides better stability:

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
        }
        androidMain.dependencies {
            implementation("app.cash.sqldelight:android-driver:2.0.2")
        }
        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:2.0.2")
        }
    }
}

// commonMain - Driver factory interface
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = AppDatabase.Schema, context = context, name = "app.db"
    )
}

// iosMain
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = AppDatabase.Schema, name = "app.db"
    )
}
```

### Dependency injection with Koin

**Koin 4.2.0** is the most widely adopted DI framework for KMP. The pattern separates common modules from platform-specific ones:

```kotlin
// commonMain
val commonModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory { GetUserUseCase(get()) }
    viewModel { HomeViewModel(get()) }
}

expect val platformModule: Module

fun appModules() = listOf(commonModule, platformModule)

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

### Firebase integration with GitLive SDK

The **GitLive Firebase SDK** provides the most complete KMP wrapper for Firebase services:

```kotlin
// commonMain dependencies
implementation("dev.gitlive:firebase-auth:2.0.0")
implementation("dev.gitlive:firebase-firestore:2.0.0")

// Shared Firebase repository
class AuthRepository {
    private val auth = Firebase.auth
    
    suspend fun signIn(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password)
    
    val currentUser: Flow<FirebaseUser?> = auth.authStateChanged
}

@Serializable
data class User(val id: String, val name: String, val email: String)

class FirestoreRepository {
    private val firestore = Firebase.firestore
    
    suspend fun getUser(id: String): User = 
        firestore.collection("users").document(id).get().data()
}
```

Platform initialization remains native: Android uses `Firebase.initialize(context)` in Application class; iOS calls `FirebaseApp.configure()` in AppDelegate before shared code executes.

### Coroutine dispatchers

With Kotlin's new memory model (1.7.20+), **dispatchers work identically across platforms**—no expect/actual needed:

```kotlin
// commonMain - Works on all platforms
class DataRepository(private val api: ApiService) {
    suspend fun fetchData(): Result<Data> = withContext(Dispatchers.IO) {
        api.getData()
    }
}
```

For testing, inject a `DispatcherProvider` interface rather than using dispatchers directly.

## Learning from production architectures

Notable companies and their architectural decisions provide valuable patterns:

| Company | Shared Code | Key Insight |
|---------|-------------|-------------|
| **Cash App** | Persistence, pure functions | Created SQLDelight; 7+ years in production. "Repository approach" for gradual adoption |
| **Netflix** | Business logic SDK | Lightweight SDK approach; started with studio production apps |
| **McDonald's** | Domain layer, payments | "Clean Architecture + DI = perfect KMP fit"; unified mobile team |
| **Google Docs** | Business logic | Migrated from Java multiplatform to KMP; "very happy with it" |
| **Bitkey (Block)** | 95% mobile codebase | Migrated to Compose Multiplatform for unified UI |

**Recommended open-source references**:
- **PeopleInSpace** (github.com/joreilly/PeopleInSpace) — Multi-platform showcase with Ktor, Koin, SQLDelight; featured in Google Dev Library
- **KaMPKit** (github.com/touchlab/KaMPKit) — Touchlab's best-practice starter with SKIE for Swift interop
- **Kotlin/kmp-production-sample** — Official RSS reader demonstrating Redux architecture, available on app stores

## Critical 2024-2025 updates

### Kotlin 2.0 and K2 compiler changes

The **K2 compiler** delivers **up to 2x faster compilation** and is now default. Key migration requirements:

```kotlin
// REQUIRED: Rename android to androidTarget (error in 2.1.0+)
kotlin {
    androidTarget { }  // Not android { }
    iosArm64()
    iosSimulatorArm64()
}
```

### Default hierarchy template

Since Kotlin 1.9.20, the Gradle plugin **automatically creates intermediate source sets** (`iosMain`, `appleMain`, `nativeMain`) based on declared targets—no manual `dependsOn()` configuration needed:

```kotlin
kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    // Automatically creates: commonMain → iosMain → iosArm64Main, etc.
    
    sourceSets {
        iosMain.dependencies {  // Direct access, no "by getting"
            implementation("io.ktor:ktor-client-darwin:2.3.11")
        }
    }
}
```

### AndroidX libraries now multiplatform

Google announced official KMP support at I/O 2024. **Lifecycle ViewModel 2.8.0+**, **Room 2.7.0+**, and **DataStore 1.2.0+** now work across platforms, enabling seamless integration with existing Android architecture components.

## Practical recommendations for your project

For a mobile app with Firebase, local database, and clean architecture, implement this stack:

- **Architecture**: MVI with Clean Architecture layers; shared ViewModels in commonMain
- **Database**: SQLDelight 2.0.2 for stability, or Room 2.7.0+ if you prefer AndroidX ecosystem
- **Networking**: Ktor 2.3.x with platform-specific engines (OkHttp/Darwin)
- **DI**: Koin 4.2.0 with platform modules for driver/engine injection
- **Firebase**: GitLive SDK for auth/firestore; native initialization per platform
- **State management**: StateFlow with single UiState per screen
- **Structure**: Start single-module, split to feature modules when reaching 5+ features

The key principle validated by production apps: **share business logic aggressively, keep UI platform-specific initially**. Companies like Cash App began with networking and data layers, expanding shared code incrementally behind feature flags. This pragmatic approach minimizes risk while maximizing code reuse.