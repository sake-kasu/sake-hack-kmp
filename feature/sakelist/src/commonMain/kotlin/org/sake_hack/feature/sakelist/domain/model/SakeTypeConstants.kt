package org.sake_hack.feature.sakelist.domain.model

/**
 * 酒の種類の定数定義
 * フィルターUIで使用する固定値
 */
object SakeTypeConstants {
    const val SAKE = "日本酒"
    const val WHISKEY = "ウイスキー"
    const val WINE = "ワイン"
    const val BEER = "ビール"

    /**
     * 全種類のリスト
     */
    val ALL = listOf(SAKE, WHISKEY, WINE, BEER)
}
