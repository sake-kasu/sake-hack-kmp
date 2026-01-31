package org.sake_hack.feature.sakelist.data.remote.dto

import kotlinx.serialization.Serializable
import org.sake_hack.feature.sakelist.domain.model.DrinkStyle

@Serializable
data class DrinkStyleDto(
    val id: Int,
    val name: String,
    val description: String? = null
) {
    fun toDomain(): DrinkStyle = DrinkStyle(
        id = id,
        name = name,
        description = description
    )
}
