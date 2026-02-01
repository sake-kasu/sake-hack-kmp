package org.sake_hack.core.common.di

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Koin DI コンテナを初期化するヘルパー
 *
 * iOS (Swift) から呼び出して KMP 側の DI をセットアップ
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("KoinHelper")
object KoinHelper {

    private var isInitialized = false

    /**
     * Koin を初期化
     *
     * iOS アプリ起動時に呼び出す
     * 各featureモジュールからDIモジュールを渡す
     */
    @ObjCName("initKoin")
    fun initKoin(
        sakelistModule: Module,
        sakeregistrationModule: Module
    ) {
        if (!isInitialized) {
            startKoin {
                modules(sakelistModule, sakeregistrationModule)
            }
            isInitialized = true
        }
    }

    /**
     * Koin をクリーンアップ
     *
     * テストや再初期化時に使用
     */
    @ObjCName("clearKoin")
    fun clearKoin() {
        // Koin のクリーンアップ処理
        // 実装は必要に応じて
        isInitialized = false
    }
}
