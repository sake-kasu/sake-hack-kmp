package org.sake_hack.feature.stocklist.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 在庫ページレスポンスDTO
 */
@Serializable
data class StockPageResponseDto(
    val data: List<StockDto>,
    val meta: MetaDto
)

/**
 * メタ情報DTO
 */
@Serializable
data class MetaDto(
    val total: Long,
    val offset: Int,
    val limit: Int
)
