package org.sake_hack.feature.sakelist.domain.model

/**
 * 酒の種類を表すドメインモデル
 * APIの type オブジェクトに対応
 */
data class SakeType(
    val id: Int,
    val name: String
)
