package org.sake_hack.feature.sakelist.domain.model

import kotlinx.datetime.Instant

/**
 * 酒（アルコール飲料）を表すドメインモデル
 * バックエンドAPIの Sake レスポンスに対応
 */
data class Sake(
    val id: Int,
    val type: SakeType,
    val brewery: Brewery,
    val name: String,
    val abv: Float,
    val tasteNotes: String,
    val memo: String?,
    val drinkStyles: List<DrinkStyle>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false
)
