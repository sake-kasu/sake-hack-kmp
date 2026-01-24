package org.sake_hack.feature.sakelist.domain.model

/**
 * 酒造を表すドメインモデル
 * APIの brewery オブジェクトに対応
 */
data class Brewery(
    val id: Int,
    val name: String,
    val originCountry: String,
    val originRegion: String?,
    val latitude: Double?,
    val longitude: Double?
)
