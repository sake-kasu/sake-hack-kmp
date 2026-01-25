package org.sake_hack.feature.stocklist.data.mapper

import kotlinx.datetime.Instant
import org.sake_hack.feature.stocklist.data.remote.dto.StockDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockEditRequestDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockPageResponseDto
import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockPageResult

/**
 * StockDto から Stock への変換
 */
fun StockDto.toDomain(): Stock = Stock(
    id = id,
    name = name,
    kana = kana,
    mainCategory = mainCategory,
    subCategory = subCategory,
    region = region,
    abv = abv,
    initialVolume = initialVolume,
    remainingVolumePercent = remainingVolumePercent,
    purchasePrice = purchasePrice,
    notes = notes,
    imageUrl = imageUrl,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)

/**
 * StockPageResponseDto から StockPageResult への変換
 */
fun StockPageResponseDto.toDomain(): StockPageResult = StockPageResult(
    items = data.map { it.toDomain() },
    total = meta.total,
    offset = meta.offset,
    limit = meta.limit
)

/**
 * StockEditRequest から StockEditRequestDto への変換
 */
fun StockEditRequest.toDto(): StockEditRequestDto = StockEditRequestDto(
    name = name,
    kana = kana,
    mainCategory = mainCategory,
    subCategory = subCategory,
    region = region,
    abv = abv,
    initialVolume = initialVolume,
    remainingVolumePercent = remainingVolumePercent,
    purchasePrice = purchasePrice,
    notes = notes,
    imageUrl = imageUrl
)

/**
 * Stock から StockDto への変換(モックAPI用)
 */
fun Stock.toDto(): StockDto = StockDto(
    id = id,
    name = name,
    kana = kana,
    mainCategory = mainCategory,
    subCategory = subCategory,
    region = region,
    abv = abv,
    initialVolume = initialVolume,
    remainingVolumePercent = remainingVolumePercent,
    purchasePrice = purchasePrice,
    notes = notes,
    imageUrl = imageUrl,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)
