package org.sake_hack.feature.stocklist.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 在庫DTO
 */
@Serializable
data class StockDto(
    val id: Int,
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
    val imageUrl: String? = null,
    val createdAt: String,
    val updatedAt: String
)
