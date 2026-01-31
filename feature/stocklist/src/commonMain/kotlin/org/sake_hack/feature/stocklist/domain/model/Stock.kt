package org.sake_hack.feature.stocklist.domain.model

import kotlinx.datetime.Instant

/**
 * 在庫のドメインモデル
 *
 * @property id 在庫ID
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
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
data class Stock(
    val id: Int,
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
    val imageUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
