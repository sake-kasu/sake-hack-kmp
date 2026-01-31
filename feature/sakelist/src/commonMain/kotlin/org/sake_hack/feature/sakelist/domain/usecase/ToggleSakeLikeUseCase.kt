package org.sake_hack.feature.sakelist.domain.usecase

import org.sake_hack.feature.sakelist.domain.repository.SakeRepository

/**
 * 酒のいいねをトグルするUseCase
 *
 * @param repository SakeRepository
 */
class ToggleSakeLikeUseCase(
    private val repository: SakeRepository
) {
    /**
     * いいねのトグル処理
     *
     * @param sakeId 酒のID
     * @param isCurrentlyLiked 現在いいねしているか
     * @return 処理結果
     */
    suspend operator fun invoke(sakeId: Int, isCurrentlyLiked: Boolean): Result<Unit> {
        return if (isCurrentlyLiked) {
            repository.unlikeSake(sakeId)
        } else {
            repository.likeSake(sakeId)
        }
    }
}
