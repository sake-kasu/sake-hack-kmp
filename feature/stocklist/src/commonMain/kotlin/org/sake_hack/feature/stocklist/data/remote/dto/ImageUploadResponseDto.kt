package org.sake_hack.feature.stocklist.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 画像アップロードレスポンスDTO
 */
@Serializable
data class ImageUploadResponseDto(
    val imageUrl: String
)
