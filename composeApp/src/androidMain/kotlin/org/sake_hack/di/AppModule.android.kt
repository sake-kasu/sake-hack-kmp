package org.sake_hack.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.sake_hack.core.database.DatabaseDriverFactory

/**
 * Android固有のKoinモジュール
 */
actual val platformModule: Module = module {
    single { DatabaseDriverFactory(get()) }
}
