package org.sake_hack.feature.sakelist.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.sake_hack.feature.sakelist.domain.usecase.GetSakePageUseCase
import org.sake_hack.feature.sakelist.presentation.SakeListViewModel

/**
 * SakeList フィーチャーの DI モジュール
 */
val sakelistModule: Module = module {
    factory { GetSakePageUseCase(get(), get(), get()) }

    factory { SakeListViewModel(get()) }
}
