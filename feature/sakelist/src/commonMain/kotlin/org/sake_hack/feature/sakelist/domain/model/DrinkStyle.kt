package org.sake_hack.feature.sakelist.domain.model

/**
 * 飲み方を表すドメインモデル
 * APIの drinkStyle オブジェクトに対応
 */
data class DrinkStyle(
    val id: Int,
    val name: String,
    val description: String?
)
