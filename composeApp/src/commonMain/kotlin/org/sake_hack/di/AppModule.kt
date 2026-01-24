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

/**
 * 全プラットフォーム共通のKoinモジュール
 */
val commonModule = module {
    // ViewModels
    viewModel { SakeListViewModel(get()) }

    // Use Cases
    factory { GetSakeListUseCase(get()) }
    factory { GetSakePageUseCase(get()) }

    // Repository
    single<SakeRepository> { SakeRepositoryImpl(get(), get()) }

    // Data Sources
    single { SakeApiService(get()) }
    single { SakeLocalDataSource(get()) }

    // HTTP Client
    single<HttpClient> { createAppHttpClient() }

    // Database
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        SakeDatabase(driver)
    }
}

/**
 * プラットフォーム固有のモジュール（expect宣言）
 * DatabaseDriverFactoryなどプラットフォーム固有の依存関係を提供
 */
expect val platformModule: Module

/**
 * 全モジュールを結合
 */
fun appModules() = listOf(commonModule, platformModule)
