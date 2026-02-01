package org.sake_hack.feature.sakelist.data.repository

import kotlinx.datetime.Clock
import org.sake_hack.feature.sakelist.domain.model.Brewery
import org.sake_hack.feature.sakelist.domain.model.DrinkStyle
import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.model.SakePageResult
import org.sake_hack.feature.sakelist.domain.model.SakeRepository
import org.sake_hack.feature.sakelist.domain.model.SakeType

/**
 * モックRepository（簡易版実装用）
 * サンプルデータを返す
 */
class SakeRepositoryMock : SakeRepository {

    private val sampleSakes = listOf(
        Sake(
            id = 1,
            type = SakeType.GINJO,
            brewery = Brewery(id = 1, name = "旭酒造", prefecture = "山口県"),
            name = "獺祭 純米大吟醸45",
            abv = 16.0f,
            tasteNotes = "きれいで華やかな香り",
            memo = "山田錦を45%まで磨き上げた純米大吟醸",
            drinkStyles = listOf(DrinkStyle.Straight),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            imageUrl = null
        ),
        Sake(
            id = 2,
            type = SakeType.HONJOZO,
            brewery = Brewery(id = 2, name = "高木酒造", prefecture = "山形県"),
            name = "十四代 本丸",
            abv = 15.0f,
            tasteNotes = "コクがあり飲みごたえあり",
            memo = "幻の銘酒と呼ばれる十四代の定番",
            drinkStyles = listOf(DrinkStyle.Straight),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            imageUrl = null
        ),
        Sake(
            id = 3,
            type = SakeType.JUNMAI_GINJO,
            brewery = Brewery(id = 3, name = "木屋正酒造", prefecture = "三重県"),
            name = "而今 純米吟醸",
            abv = 16.0f,
            tasteNotes = "フレッシュで華やかな香り",
            memo = "若き醸造家による革新の日本酒",
            drinkStyles = listOf(DrinkStyle.Straight),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            imageUrl = null
        ),
        Sake(
            id = 4,
            type = SakeType.JUNMAI,
            brewery = Brewery(id = 4, name = "新政酒造", prefecture = "秋田県"),
            name = "新政 No.6",
            abv = 15.0f,
            tasteNotes = "酸味がありフレッシュ",
            memo = "6号酵母で醸す革新的な日本酒",
            drinkStyles = listOf(DrinkStyle.Straight),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            imageUrl = null
        ),
        Sake(
            id = 5,
            type = SakeType.DAI_GINJO,
            brewery = Brewery(id = 5, name = "黒龍酒造", prefecture = "福井県"),
            name = "黒龍 大吟醸",
            abv = 15.5f,
            tasteNotes = "上品で繊細な味わい",
            memo = "福井を代表する銘醸蔵の大吟醸",
            drinkStyles = listOf(DrinkStyle.Straight),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            imageUrl = null
        )
    )

    override suspend fun getAllSake(): Result<List<Sake>> {
        return Result.success(sampleSakes)
    }

    override suspend fun getSakeById(id: String): Result<Sake> {
        val sake = sampleSakes.find { it.id.toString() == id }
        return if (sake != null) {
            Result.success(sake)
        } else {
            Result.failure(Exception("Sake not found: $id"))
        }
    }

    override suspend fun refreshSakeList(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getSakePage(
        offset: Int,
        limit: Int,
        filterCriteria: FilterCriteria?
    ): Result<SakePageResult> {
        // フィルター適用
        var filtered = sampleSakes

        if (filterCriteria != null) {
            // 酒名フィルター
            if (!filterCriteria.sakeName.isNullOrEmpty()) {
                filtered = filtered.filter { it.name.contains(filterCriteria.sakeName, ignoreCase = true) }
            }

            // タイプフィルター
            if (filterCriteria.sakeTypes.isNotEmpty()) {
                filtered = filtered.filter { sake ->
                    filterCriteria.sakeTypes.any { type ->
                        when (type) {
                            "純米大吟醸" -> sake.type == SakeType.GINJO
                            "大吟醸" -> sake.type == SakeType.DAI_GINJO
                            "純米吟醸" -> sake.type == SakeType.JUNMAI_GINJO
                            "吟醸" -> sake.type == SakeType.GINJO
                            "純米" -> sake.type == SakeType.JUNMAI
                            "本醸造" -> sake.type == SakeType.HONJOZO
                            else -> true
                        }
                    }
                }
            }

            // 地域フィルター
            if (!filterCriteria.region.isNullOrEmpty()) {
                filtered = filtered.filter { it.brewery.prefecture.contains(filterCriteria.region, ignoreCase = true) }
            }
        }

        // ページネーション
        val total = filtered.size.toLong()
        val items = filtered.drop(offset).take(limit)

        return Result.success(
            SakePageResult(
                items = items,
                offset = offset + items.size,
                limit = limit,
                total = total
            )
        )
    }
}
