package org.sake_hack.feature.sakelist.domain.model

import org.sake_hack.feature.sakelist.domain.model.Sake

/**
 * 並べ替えオプション
 * デザイン仕様: sake_list.pen - 並べ替えメニュー
 */
enum class SortOption(val displayName: String) {
    NAME_ASC("酒名（昇順）"),
    NAME_DESC("酒名（降順）"),
    ABV_ASC("度数（低→高）"),
    ABV_DESC("度数（高→低）");

    /**
     * Sakeリストをこのオプションでソート
     */
    fun sort(sakes: List<Sake>): List<Sake> {
        return when (this) {
            NAME_ASC -> sakes.sortedBy { it.name }
            NAME_DESC -> sakes.sortedByDescending { it.name }
            ABV_ASC -> sakes.sortedBy { it.abv }
            ABV_DESC -> sakes.sortedByDescending { it.abv }
        }
    }

    companion object {
        /**
         * デフォルトの並べ替えオプション
         */
        val DEFAULT = NAME_ASC
    }
}
