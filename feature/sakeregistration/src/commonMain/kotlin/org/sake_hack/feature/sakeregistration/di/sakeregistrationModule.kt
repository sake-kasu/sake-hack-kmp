package org.sake_hack.feature.sakeregistration.di

import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.sake_hack.feature.sakeregistration.data.remote.SakeRegistrationApiService
import org.sake_hack.feature.sakeregistration.data.repository.SakeRegistrationRepositoryImpl
import org.sake_hack.feature.sakeregistration.domain.repository.SakeRegistrationRepository
import org.sake_hack.feature.sakeregistration.domain.usecase.CreateSakeUseCase

/**
 * SakeRegistration フィーチャーの DI モジュール
 * Note: Presentation層はSwift側で実装するため含まれない
 */
val sakeregistrationModule: Module = module {
    // Data層
    single { SakeRegistrationApiService(get()) }
    single<SakeRegistrationRepository> { SakeRegistrationRepositoryImpl(get()) }

    // Domain層
    factory { CreateSakeUseCase(get()) }
}
