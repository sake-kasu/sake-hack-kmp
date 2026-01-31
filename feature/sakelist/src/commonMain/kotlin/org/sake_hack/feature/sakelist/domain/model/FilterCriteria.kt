package org.sake_hack.feature.sakelist.domain.model

/**
 * 酒一覧のフィルタリング条件
 * バックエンドAPIのクエリパラメータに対応
 */
data class FilterCriteria(
    val sakeName: String? = null,
    val sakeTypes: Set<String> = emptySet(),
    val region: String? = null
) {
    /**
     * フィルターが有効かどうかを確認
     */
    fun isActive(): Boolean {
        return !sakeName.isNullOrBlank() || sakeTypes.isNotEmpty() || !region.isNullOrBlank()
    }

    /**
     * 有効なフィルターの数をカウント
     */
    fun activeFilterCount(): Int {
        return (if (!sakeName.isNullOrBlank()) 1 else 0) +
                (if (sakeTypes.isNotEmpty()) 1 else 0) +
                (if (!region.isNullOrBlank()) 1 else 0)
    }
}
