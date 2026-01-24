package org.sake_hack.feature.sakelist.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * ページネーション対応のAPIレスポンスDTO
 * バックエンドAPIの ListSakesResponse に対応
 */
@Serializable
data class SakePageResponseDto(
    val data: List<SakeDto>,
    val meta: SakeListMetaDto
)

@Serializable
data class SakeListMetaDto(
    val total: Long,
    val offset: Int,
    val limit: Int
)
