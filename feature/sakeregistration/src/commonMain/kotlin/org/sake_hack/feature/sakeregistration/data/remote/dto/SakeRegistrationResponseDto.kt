package org.sake_hack.feature.sakeregistration.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.sake_hack.feature.sakeregistration.domain.model.SakeRegistrationResult

/**
 * 酒登録レスポンスのDTO
 * バックエンドAPIのレスポンスに対応
 */
@Serializable
data class SakeRegistrationResponseDto(
    @SerialName("id")
    val id: String
) {
    /**
     * DTOをドメインモデルに変換
     */
    fun toDomain(): SakeRegistrationResult {
        return SakeRegistrationResult(
            id = id
        )
    }
}
