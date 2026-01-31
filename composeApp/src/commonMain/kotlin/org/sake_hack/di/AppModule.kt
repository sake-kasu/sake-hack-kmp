package org.sake_hack.di

import io.ktor.client.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.sake_hack.core.database.DatabaseDriverFactory
import org.sake_hack.core.database.SakeDatabase
import org.sake_hack.core.network.createAppHttpClient
import org.sake_hack.feature.sakelist.data.local.SakeLocalDataSource
import org.sake_hack.feature.sakelist.data.remote.SakeApiService
import org.sake_hack.feature.sakelist.data.repository.SakeRepositoryImpl
import org.sake_hack.feature.sakelist.domain.repository.SakeRepository
import org.sake_hack.feature.sakelist.domain.usecase.GetSakeListUseCase
import org.sake_hack.feature.sakelist.domain.usecase.GetSakePageUseCase
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
    viewModel { SakeListViewModel(get()) }
    viewModel { StockListViewModel(get(), get(), get(), get(), get()) }
    viewModel { DrawerViewModel() }

    // Use Cases
    factory { GetSakeListUseCase(get()) }
    factory { GetSakePageUseCase(get()) }
    factory { GetStockPageUseCase(get()) }
    factory { GetStockByIdUseCase(get()) }
    factory { CreateStockUseCase(get()) }
    factory { UpdateStockUseCase(get()) }
    factory { UploadStockImageUseCase(get()) }

    // Repository
    single<SakeRepository> { SakeRepositoryImpl(get(), get()) }
    single<StockRepository> { StockRepositoryImpl(get()) }

    // Data Sources
    single { SakeApiService(get()) }
    single { SakeLocalDataSource(get()) }
    single { StockApiService() }

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
