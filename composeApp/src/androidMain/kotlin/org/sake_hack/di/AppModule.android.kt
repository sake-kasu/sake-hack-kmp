package org.sake_hack.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.sake_hack.core.database.DatabaseDriverFactory
import org.sake_hack.feature.auth.data.TokenManager

/**
 * Android固有のKoinモジュール
 */
actual val platformModule: Module = module {
    single { DatabaseDriverFactory(get()) }
    single { TokenManager(get()) }
}
