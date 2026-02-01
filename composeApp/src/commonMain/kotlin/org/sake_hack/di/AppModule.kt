package org.sake_hack.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.sake_hack.core.database.DatabaseDriverFactory
import org.sake_hack.core.database.SakeDatabase
import org.sake_hack.core.network.createAppHttpClient
import org.sake_hack.feature.auth.data.repository.AuthRepositoryImpl
import org.sake_hack.feature.auth.domain.repository.AuthRepository
import org.sake_hack.feature.auth.domain.usecase.*
import org.sake_hack.feature.auth.presentation.LoginViewModel
import org.sake_hack.feature.auth.presentation.LogoutViewModel
import org.sake_hack.feature.auth.presentation.SessionViewModel
import org.sake_hack.feature.sakelist.data.local.SakeLocalDataSource
import org.sake_hack.feature.sakelist.data.remote.SakeApiService
import org.sake_hack.feature.sakelist.data.repository.SakeRepositoryImpl
import org.sake_hack.feature.sakelist.domain.repository.SakeRepository
import org.sake_hack.feature.sakelist.domain.usecase.GetSakeListUseCase
import org.sake_hack.feature.sakelist.domain.usecase.GetSakePageUseCase
import org.sake_hack.feature.sakelist.domain.usecase.ToggleSakeLikeUseCase
import org.sake_hack.feature.sakelist.presentation.SakeListViewModel
import org.sake_hack.feature.stocklist.data.remote.StockApiService
import org.sake_hack.feature.stocklist.data.repository.StockRepositoryImpl
import org.sake_hack.feature.stocklist.domain.repository.StockRepository
import org.sake_hack.feature.stocklist.domain.usecase.*
import org.sake_hack.feature.stocklist.presentation.StockListViewModel
import org.sake_hack.ui.drawer.DrawerViewModel

/**
 * 全プラットフォーム共通のKoinモジュール
 */
val commonModule = module {
    // ViewModels
    viewModel { SakeListViewModel(get(), get()) }
    viewModel { StockListViewModel(get(), get(), get(), get(), get()) }
    viewModel { DrawerViewModel() }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { LogoutViewModel(get()) }
    viewModel { SessionViewModel(get()) }

    // Use Cases - Auth
    factory { LoginUseCase(get()) }
    factory { LoginAsGuestUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { IsSessionValidUseCase(get()) }

    // Use Cases - Sake
    factory { GetSakeListUseCase(get()) }
    factory { GetSakePageUseCase(get()) }
    factory { ToggleSakeLikeUseCase(get()) }

    // Use Cases - Stock
    factory { GetStockPageUseCase(get()) }
    factory { GetStockByIdUseCase(get()) }
    factory { CreateStockUseCase(get()) }
    factory { UpdateStockUseCase(get()) }
    factory { UploadStockImageUseCase(get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(Firebase.auth, get()) }
    single<SakeRepository> { SakeRepositoryImpl(get(), get()) }
    single<StockRepository> { StockRepositoryImpl(get()) }

    // Data Sources
    single { SakeApiService(get()) }
    single { SakeLocalDataSource(get()) }
    single { StockApiService(get()) }

    // HTTP Client
    single<HttpClient> { createAppHttpClient() }

    // Database
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        SakeDatabase(driver)
    }
}

/**
 * プラットフォーム固有のモジュール(expect宣言)
 * DatabaseDriverFactoryなどプラットフォーム固有の依存関係を提供
 */
expect val platformModule: Module

/**
 * 全モジュールを結合
 */
fun appModules() = listOf(commonModule, platformModule)
