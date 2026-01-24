package org.sake_hack.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.sake_hack.core.database.DatabaseDriverFactory

/**
 * iOS固有のKoinモジュール
 */
actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
}
