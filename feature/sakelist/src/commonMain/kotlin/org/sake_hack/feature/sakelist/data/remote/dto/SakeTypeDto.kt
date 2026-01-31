package org.sake_hack.feature.sakelist.data.remote.dto

import kotlinx.serialization.Serializable
import org.sake_hack.feature.sakelist.domain.model.SakeType

@Serializable
data class SakeTypeDto(
    val id: Int,
    val name: String
) {
    fun toDomain(): SakeType = SakeType(
        id = id,
        name = name
    )
}
