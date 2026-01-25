package org.sake_hack.feature.stocklist.domain.model

/**
 * 在庫編集リクエスト
 *
 * @property name 酒名(必須、最大100文字、記号禁止)
 * @property kana よみがな(必須、最大100文字、記号禁止)
 * @property mainCategory 大分類(必須、選択式)
 * @property subCategory 小分類(オプショナル、最大100文字、記号禁止)
 * @property region 産地(オプショナル、コンボボックス)
 * @property abv 度数(必須、整数、0〜100)
 * @property initialVolume 購入時容量(必須、整数、0〜10000mL)
 * @property remainingVolumePercent 残容量(必須、選択式、0,25,50,75,100)
 * @property purchasePrice 購入時価格(必須、整数、0〜1000000円)
 * @property notes 自由記述(オプショナル、最大500文字、記号・絵文字OK)
 * @property imageUrl 画像URL(オプショナル、ファイルサイズ10MB以下)
 */
data class StockEditRequest(
    val name: String,
    val kana: String,
    val mainCategory: String,
    val subCategory: String?,
    val region: String?,
    val abv: Int,
    val initialVolume: Int,
    val remainingVolumePercent: Int,
    val purchasePrice: Int,
    val notes: String?,
    val imageUrl: String?
)

/**
 * 在庫編集フィールド
 */
enum class StockEditField {
    /** 酒名 */
    NAME,

    /** よみがな */
    KANA,

    /** 大分類 */
    MAIN_CATEGORY,

    /** 小分類 */
    SUB_CATEGORY,

    /** 産地 */
    REGION,

    /** 度数 */
    ABV,

    /** 購入時容量 */
    INITIAL_VOLUME,

    /** 残容量 */
    REMAINING_VOLUME,

    /** 購入時価格 */
    PURCHASE_PRICE,

    /** 自由記述 */
    NOTES,

    /** 画像 */
    IMAGE
}
