package org.sake_hack.feature.sakelist.data.remote.dto

import kotlinx.serialization.Serializable
import org.sake_hack.feature.sakelist.domain.model.Brewery

@Serializable
data class BreweryDto(
    val id: Int,
    val name: String,
    val originCountry: String,
    val originRegion: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun toDomain(): Brewery = Brewery(
        id = id,
        name = name,
        originCountry = originCountry,
        originRegion = originRegion,
        latitude = latitude,
        longitude = longitude
    )
}
