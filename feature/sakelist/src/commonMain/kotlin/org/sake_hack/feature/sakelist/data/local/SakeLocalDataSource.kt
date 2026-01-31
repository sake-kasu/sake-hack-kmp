package org.sake_hack.feature.sakelist.data.local

import kotlinx.datetime.Instant
import org.sake_hack.core.database.SakeDatabase
import org.sakehack.core.database.GetAllSake
import org.sakehack.core.database.GetSakeById
import org.sake_hack.feature.sakelist.domain.model.Brewery
import org.sake_hack.feature.sakelist.domain.model.DrinkStyle
import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.model.SakeType

/**
 * SQLDelightを使用した酒のローカルデータソース
 * 新しいAPIモデルに対応
 */
class SakeLocalDataSource(private val database: SakeDatabase) {
    private val queries = database.sakeDatabaseQueries

    /**
     * ローカルデータベースから全ての酒を取得
     */
    fun getAllSake(): List<Sake> {
        val sakeList = queries.getAllSake().executeAsList()
        return sakeList.map { dbSake ->
            val drinkStyles = queries.getDrinkStylesBySakeId(dbSake.id.toLong()).executeAsList()
            dbSake.toSake(drinkStyles)
        }
    }

    /**
     * IDで特定の酒を取得
     */
    fun getSakeById(id: String): Sake? {
        val dbSake = queries.getSakeById(id.toLong()).executeAsOneOrNull() ?: return null
        val drinkStyles = queries.getDrinkStylesBySakeId(id.toLong()).executeAsList()
        return dbSake.toSake(drinkStyles)
    }

    /**
     * 酒のリストをローカルデータベースに保存
     */
    fun saveSakeList(sakeList: List<Sake>) {
        sakeList.forEach { sake ->
            // SakeTypeを保存
            queries.insertSakeType(
                id = sake.type.id.toLong(),
                name = sake.type.name
            )

            // Breweryを保存
            queries.insertBrewery(
                id = sake.brewery.id.toLong(),
                name = sake.brewery.name,
                originCountry = sake.brewery.originCountry,
                originRegion = sake.brewery.originRegion,
                latitude = sake.brewery.latitude,
                longitude = sake.brewery.longitude
            )

            // DrinkStyleを保存
            sake.drinkStyles.forEach { drinkStyle ->
                queries.insertDrinkStyle(
                    id = drinkStyle.id.toLong(),
                    name = drinkStyle.name,
                    description = drinkStyle.description
                )
            }

            // Sakeを保存
            queries.insertSake(
                id = sake.id.toLong(),
                typeId = sake.type.id.toLong(),
                breweryId = sake.brewery.id.toLong(),
                name = sake.name,
                abv = sake.abv.toDouble(),
                tasteNotes = sake.tasteNotes,
                memo = sake.memo,
                createdAt = sake.createdAt.toEpochMilliseconds(),
                updatedAt = sake.updatedAt.toEpochMilliseconds(),
                imageUrl = sake.imageUrl
            )

            // SakeDrinkStyleを保存
            sake.drinkStyles.forEach { drinkStyle ->
                queries.insertSakeDrinkStyle(
                    sakeId = sake.id.toLong(),
                    drinkStyleId = drinkStyle.id.toLong()
                )
            }
        }
    }

    /**
     * ローカルデータベースから全ての酒を削除
     */
    fun deleteAll() {
        queries.deleteAll()
    }

    /**
     * IDで特定の酒を削除
     */
    fun deleteSakeById(id: String) {
        queries.deleteSakeById(id.toLong())
    }

    /**
     * データベースクエリ結果をドメインモデルに変換
     */
    private fun GetAllSake.toSake(
        drinkStyles: List<org.sakehack.core.database.DrinkStyle>
    ): Sake {
        return Sake(
            id = id.toInt(),
            type = SakeType(
                id = typeId.toInt(),
                name = typeName
            ),
            brewery = Brewery(
                id = breweryId.toInt(),
                name = breweryName,
                originCountry = originCountry,
                originRegion = originRegion,
                latitude = latitude,
                longitude = longitude
            ),
            name = name,
            abv = abv.toFloat(),
            tasteNotes = tasteNotes,
            memo = memo,
            drinkStyles = drinkStyles.map {
                DrinkStyle(
                    id = it.id.toInt(),
                    name = it.name,
                    description = it.description
                )
            },
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt),
            imageUrl = imageUrl
        )
    }

    /**
     * GetSakeByIdクエリ結果をドメインモデルに変換
     */
    private fun GetSakeById.toSake(
        drinkStyles: List<org.sakehack.core.database.DrinkStyle>
    ): Sake {
        return Sake(
            id = id.toInt(),
            type = SakeType(
                id = typeId.toInt(),
                name = typeName
            ),
            brewery = Brewery(
                id = breweryId.toInt(),
                name = breweryName,
                originCountry = originCountry,
                originRegion = originRegion,
                latitude = latitude,
                longitude = longitude
            ),
            name = name,
            abv = abv.toFloat(),
            tasteNotes = tasteNotes,
            memo = memo,
            drinkStyles = drinkStyles.map {
                DrinkStyle(
                    id = it.id.toInt(),
                    name = it.name,
                    description = it.description
                )
            },
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt),
            imageUrl = imageUrl
        )
    }
}
