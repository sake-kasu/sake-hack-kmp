package org.sake_hack.feature.stocklist.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 在庫編集リクエストDTO
 */
@Serializable
data class StockEditRequestDto(
    val name: String,
    val kana: String,
    val mainCategory: String,
    val subCategory: String? = null,
    val region: String? = null,
    val abv: Int,
    val initialVolume: Int,
    val remainingVolumePercent: Int,
    val purchasePrice: Int,
    val notes: String? = null,
    val imageUrl: String? = null
)
