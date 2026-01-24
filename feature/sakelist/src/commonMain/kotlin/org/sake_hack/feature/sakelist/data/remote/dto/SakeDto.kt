package org.sake_hack.feature.sakelist.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.sake_hack.feature.sakelist.domain.model.Sake

/**
 * APIから取得する酒のData Transfer Object
 * バックエンドAPIの Sake レスポンスに対応
 */
@Serializable
data class SakeDto(
    val id: Int,
    val type: SakeTypeDto,
    val brewery: BreweryDto,
    val name: String,
    val abv: Float,
    val tasteNotes: String,
    val memo: String? = null,
    val drinkStyles: List<DrinkStyleDto>,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * DTOをドメインモデルに変換
     */
    fun toDomain(): Sake {
        return Sake(
            id = id,
            type = type.toDomain(),
            brewery = brewery.toDomain(),
            name = name,
            abv = abv,
            tasteNotes = tasteNotes,
            memo = memo,
            drinkStyles = drinkStyles.map { it.toDomain() },
            createdAt = createdAt,
            updatedAt = updatedAt,
            imageUrl = null
        )
    }
}
