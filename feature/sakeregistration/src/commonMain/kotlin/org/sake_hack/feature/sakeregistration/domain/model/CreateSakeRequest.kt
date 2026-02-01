package org.sake_hack.feature.sakeregistration.domain.model

/**
 * 酒登録リクエストのドメインモデル
 */
data class CreateSakeRequest(
    val typeId: Int,
    val breweryId: Int,
    val name: String,
    val abv: Int?,
    val tasteNotes: String?,
    val memo: String?,
    val drinkStyleIds: List<Int>
)
