package org.sake_hack.feature.sakeregistration.data.remote.dto

import kotlinx.serialization.Serializable
import org.sake_hack.feature.sakeregistration.domain.model.CreateSakeRequest

/**
 * 酒登録リクエストのDTO
 * バックエンドAPIのリクエストボディに対応
 */
@Serializable
data class CreateSakeRequestDto(
    val typeId: Int,
    val breweryId: Int,
    val name: String,
    val abv: Int?,
    val tasteNotes: String?,
    val memo: String?,
    val drinkStyleIds: List<Int>
) {
    companion object {
        /**
         * ドメインモデルからDTOに変換
         */
        fun fromDomain(request: CreateSakeRequest): CreateSakeRequestDto {
            return CreateSakeRequestDto(
                typeId = request.typeId,
                breweryId = request.breweryId,
                name = request.name,
                abv = request.abv,
                tasteNotes = request.tasteNotes,
                memo = request.memo,
                drinkStyleIds = request.drinkStyleIds
            )
        }
    }
}
