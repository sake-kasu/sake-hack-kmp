package org.sake_hack.feature.stocklist.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import org.sake_hack.feature.stocklist.data.remote.dto.ImageUploadResponseDto
import org.sake_hack.feature.stocklist.data.remote.dto.MetaDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockEditRequestDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockPageResponseDto
import kotlin.random.Random

/**
 * 在庫APIサービス(モック実装)
 *
 * 30〜50件のサンプルデータをメモリ内で管理し、フィルター・ソート・ページネーションを実装
 */
class StockApiService {
    private var stockDatabase: MutableList<StockDto> = generateSampleData().toMutableList()
    private var nextId: Int = stockDatabase.maxOfOrNull { it.id }?.plus(1) ?: 1
    private val mutex = Mutex()

    /**
     * 在庫ページを取得
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param name 酒名でフィルタ(部分一致)
     * @param categories 種類でフィルタ(カンマ区切り)
     * @param region 産地でフィルタ(部分一致)
     * @param sortBy ソートフィールド(abv, name, remainingVolume, price)
     * @param order ソート順(asc, desc)
     * @return 在庫ページレスポンス
     */
    suspend fun getStockPage(
        offset: Int = 0,
        limit: Int = 20,
        name: String? = null,
        categories: String? = null,
        region: String? = null,
        sortBy: String = "name",
        order: String = "asc"
    ): StockPageResponseDto = mutex.withLock {
        // ネットワーク遅延シミュレート
        delay(500)

        // ランダムエラーシミュレート(10%の確率)
        if (Random.nextDouble() < 0.1) {
            throw Exception("Mock API Error: Network timeout")
        }

        // フィルタリング
        var filteredStocks = stockDatabase.toList()

        name?.let { filterName ->
            filteredStocks = filteredStocks.filter { it.name.contains(filterName, ignoreCase = true) }
        }

        categories?.let { filterCategories ->
            val categoryList = filterCategories.split(",").map { it.trim() }
            filteredStocks = filteredStocks.filter { it.mainCategory in categoryList }
        }

        region?.let { filterRegion ->
            filteredStocks = filteredStocks.filter {
                it.region?.contains(filterRegion, ignoreCase = true) == true
            }
        }

        // ソート
        filteredStocks = when (sortBy) {
            "abv" -> if (order == "asc") {
                filteredStocks.sortedBy { it.abv }
            } else {
                filteredStocks.sortedByDescending { it.abv }
            }
            "name" -> if (order == "asc") {
                filteredStocks.sortedBy { it.name }
            } else {
                filteredStocks.sortedByDescending { it.name }
            }
            "remainingVolume" -> if (order == "asc") {
                filteredStocks.sortedBy { it.remainingVolumePercent }
            } else {
                filteredStocks.sortedByDescending { it.remainingVolumePercent }
            }
            "price" -> if (order == "asc") {
                filteredStocks.sortedBy { it.purchasePrice }
            } else {
                filteredStocks.sortedByDescending { it.purchasePrice }
            }
            else -> filteredStocks
        }

        // ページネーション
        val total = filteredStocks.size.toLong()
        val paginatedStocks = filteredStocks.drop(offset).take(limit)

        return@withLock StockPageResponseDto(
            data = paginatedStocks,
            meta = MetaDto(
                total = total,
                offset = offset,
                limit = limit
            )
        )
    }

    /**
     * 在庫をIDで取得
     *
     * @param id 在庫ID
     * @return 在庫DTO
     */
    suspend fun getStockById(id: Int): StockDto = mutex.withLock {
        // ネットワーク遅延シミュレート
        delay(300)

        // ランダムエラーシミュレート(10%の確率)
        if (Random.nextDouble() < 0.1) {
            throw Exception("Mock API Error: Stock not found")
        }

        return@withLock stockDatabase.find { it.id == id }
            ?: throw Exception("Stock with ID $id not found")
    }

    /**
     * 在庫を作成
     *
     * @param request 在庫作成リクエスト
     * @return 作成された在庫DTO
     */
    suspend fun createStock(request: StockEditRequestDto): StockDto = mutex.withLock {
        // ネットワーク遅延シミュレート
        delay(500)

        // ランダムエラーシミュレート(10%の確率)
        if (Random.nextDouble() < 0.1) {
            throw Exception("Mock API Error: Failed to create stock")
        }

        val now = Clock.System.now().toString()
        val newStock = StockDto(
            id = nextId++,
            name = request.name,
            kana = request.kana,
            mainCategory = request.mainCategory,
            subCategory = request.subCategory,
            region = request.region,
            abv = request.abv,
            initialVolume = request.initialVolume,
            remainingVolumePercent = request.remainingVolumePercent,
            purchasePrice = request.purchasePrice,
            notes = request.notes,
            imageUrl = request.imageUrl,
            createdAt = now,
            updatedAt = now
        )

        stockDatabase.add(newStock)
        return@withLock newStock
    }

    /**
     * 在庫を更新
     *
     * @param id 在庫ID
     * @param request 在庫更新リクエスト
     * @return 更新された在庫DTO
     */
    suspend fun updateStock(id: Int, request: StockEditRequestDto): StockDto = mutex.withLock {
        // ネットワーク遅延シミュレート
        delay(500)

        // ランダムエラーシミュレート(10%の確率)
        if (Random.nextDouble() < 0.1) {
            throw Exception("Mock API Error: Failed to update stock")
        }

        val index = stockDatabase.indexOfFirst { it.id == id }
        if (index == -1) {
            throw Exception("Stock with ID $id not found")
        }

        val existingStock = stockDatabase[index]
        val updatedStock = StockDto(
            id = id,
            name = request.name,
            kana = request.kana,
            mainCategory = request.mainCategory,
            subCategory = request.subCategory,
            region = request.region,
            abv = request.abv,
            initialVolume = request.initialVolume,
            remainingVolumePercent = request.remainingVolumePercent,
            purchasePrice = request.purchasePrice,
            notes = request.notes,
            imageUrl = request.imageUrl,
            createdAt = existingStock.createdAt,
            updatedAt = Clock.System.now().toString()
        )

        stockDatabase[index] = updatedStock
        return@withLock updatedStock
    }

    /**
     * 在庫画像をアップロード
     *
     * @param id 在庫ID
     * @param imageData 画像データ
     * @return 画像アップロードレスポンス
     */
    suspend fun uploadStockImage(id: Int, imageData: ByteArray): ImageUploadResponseDto = mutex.withLock {
        // ネットワーク遅延シミュレート
        delay(1000)

        // ランダムエラーシミュレート(10%の確率)
        if (Random.nextDouble() < 0.1) {
            throw Exception("Mock API Error: Failed to upload image")
        }

        // モック画像URLを生成
        val mockImageUrl = "https://example.com/images/stock_${id}_${System.currentTimeMillis()}.jpg"

        // 在庫のimageUrlを更新
        val index = stockDatabase.indexOfFirst { it.id == id }
        if (index != -1) {
            val stock = stockDatabase[index]
            stockDatabase[index] = stock.copy(
                imageUrl = mockImageUrl,
                updatedAt = Clock.System.now().toString()
            )
        }

        return@withLock ImageUploadResponseDto(imageUrl = mockImageUrl)
    }

    /**
     * サンプルデータを生成(30〜50件)
     */
    private fun generateSampleData(): List<StockDto> {
        val baseTime = Clock.System.now()
        return listOf(
            // 日本酒(15件)
            StockDto(
                id = 1,
                name = "獺祭 純米大吟醸 45",
                kana = "だっさい じゅんまいだいぎんじょう 45",
                mainCategory = "日本酒",
                subCategory = "純米大吟醸",
                region = "山口県",
                abv = 16,
                initialVolume = 720,
                remainingVolumePercent = 75,
                purchasePrice = 3300,
                notes = "フルーティーで飲みやすい。華やかな香りとすっきりした後味が特徴。",
                imageUrl = "https://example.com/images/dassai.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 2,
                name = "久保田 千寿",
                kana = "くぼた せんじゅ",
                mainCategory = "日本酒",
                subCategory = "吟醸酒",
                region = "新潟県",
                abv = 15,
                initialVolume = 720,
                remainingVolumePercent = 50,
                purchasePrice = 2200,
                notes = "淡麗辛口の代表格。食事との相性が良い。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 3,
                name = "八海山 純米吟醸",
                kana = "はっかいさん じゅんまいぎんじょう",
                mainCategory = "日本酒",
                subCategory = "純米吟醸",
                region = "新潟県",
                abv = 15,
                initialVolume = 720,
                remainingVolumePercent = 100,
                purchasePrice = 2800,
                notes = "まろやかで飲みやすい。冷やでも燗でも美味しい。",
                imageUrl = "https://example.com/images/hakkaisan.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 4,
                name = "十四代 本丸",
                kana = "じゅうよんだい ほんまる",
                mainCategory = "日本酒",
                subCategory = "特別本醸造",
                region = "山形県",
                abv = 15,
                initialVolume = 1800,
                remainingVolumePercent = 25,
                purchasePrice = 15000,
                notes = "プレミアム日本酒。希少価値が高い。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 5,
                name = "而今 純米吟醸",
                kana = "じこん じゅんまいぎんじょう",
                mainCategory = "日本酒",
                subCategory = "純米吟醸",
                region = "三重県",
                abv = 16,
                initialVolume = 720,
                remainingVolumePercent = 50,
                purchasePrice = 12000,
                notes = "入手困難な銘柄。フルーティーで香り高い。",
                imageUrl = "https://example.com/images/jikon.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 6,
                name = "作 穂乃智",
                kana = "ざく ほのとも",
                mainCategory = "日本酒",
                subCategory = "純米酒",
                region = "三重県",
                abv = 15,
                initialVolume = 720,
                remainingVolumePercent = 75,
                purchasePrice = 1500,
                notes = "コストパフォーマンスが良い。バランスの取れた味わい。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 7,
                name = "鍋島 純米吟醸",
                kana = "なべしま じゅんまいぎんじょう",
                mainCategory = "日本酒",
                subCategory = "純米吟醸",
                region = "佐賀県",
                abv = 16,
                initialVolume = 720,
                remainingVolumePercent = 100,
                purchasePrice = 3500,
                notes = "IWC 2011 SAKE部門最高賞受賞。",
                imageUrl = "https://example.com/images/nabeshima.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 8,
                name = "飛露喜 特別純米",
                kana = "ひろき とくべつじゅんまい",
                mainCategory = "日本酒",
                subCategory = "特別純米",
                region = "福島県",
                abv = 16,
                initialVolume = 1800,
                remainingVolumePercent = 50,
                purchasePrice = 8000,
                notes = "芳醇旨口。入手困難な人気銘柄。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 9,
                name = "新政 No.6 S-type",
                kana = "あらまさ ナンバーシックス エスタイプ",
                mainCategory = "日本酒",
                subCategory = "純米酒",
                region = "秋田県",
                abv = 13,
                initialVolume = 720,
                remainingVolumePercent = 0,
                purchasePrice = 4500,
                notes = "低アルコール。独特な酸味が特徴。飲み終わり。",
                imageUrl = "https://example.com/images/aramasa.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 10,
                name = "田酒 特別純米",
                kana = "でんしゅ とくべつじゅんまい",
                mainCategory = "日本酒",
                subCategory = "特別純米",
                region = "青森県",
                abv = 15,
                initialVolume = 720,
                remainingVolumePercent = 75,
                purchasePrice = 3000,
                notes = "純米酒の代表格。米の旨味を感じる。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 11,
                name = "醸し人九平次 山田錦",
                kana = "かもしびとくへいじ やまだにしき",
                mainCategory = "日本酒",
                subCategory = "純米大吟醸",
                region = "愛知県",
                abv = 16,
                initialVolume = 720,
                remainingVolumePercent = 100,
                purchasePrice = 5000,
                notes = "海外でも人気。エレガントな味わい。",
                imageUrl = "https://example.com/images/kuheiji.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 12,
                name = "黒龍 大吟醸",
                kana = "こくりゅう だいぎんじょう",
                mainCategory = "日本酒",
                subCategory = "大吟醸",
                region = "福井県",
                abv = 15,
                initialVolume = 720,
                remainingVolumePercent = 50,
                purchasePrice = 4200,
                notes = "福井の銘酒。華やかな香りと綺麗な味わい。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 13,
                name = "東洋美人 純米大吟醸",
                kana = "とうようびじん じゅんまいだいぎんじょう",
                mainCategory = "日本酒",
                subCategory = "純米大吟醸",
                region = "山口県",
                abv = 16,
                initialVolume = 720,
                remainingVolumePercent = 75,
                purchasePrice = 3800,
                notes = "フルーティーで女性に人気。",
                imageUrl = "https://example.com/images/touyoubijin.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 14,
                name = "磯自慢 純米大吟醸",
                kana = "いそじまん じゅんまいだいぎんじょう",
                mainCategory = "日本酒",
                subCategory = "純米大吟醸",
                region = "静岡県",
                abv = 16,
                initialVolume = 720,
                remainingVolumePercent = 25,
                purchasePrice = 6000,
                notes = "静岡の高級酒。繊細でエレガント。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 15,
                name = "天狗舞 山廃純米",
                kana = "てんぐまい やまはいじゅんまい",
                mainCategory = "日本酒",
                subCategory = "山廃純米",
                region = "石川県",
                abv = 15,
                initialVolume = 1800,
                remainingVolumePercent = 50,
                purchasePrice = 3200,
                notes = "山廃仕込みの旨口。燗酒に最適。",
                imageUrl = "https://example.com/images/tengumai.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),

            // ウイスキー(10件)
            StockDto(
                id = 16,
                name = "山崎 12年",
                kana = "やまざき 12ねん",
                mainCategory = "ウイスキー",
                subCategory = "シングルモルト",
                region = "京都府",
                abv = 43,
                initialVolume = 700,
                remainingVolumePercent = 50,
                purchasePrice = 15000,
                notes = "日本を代表するシングルモルト。やわらかく華やか。",
                imageUrl = "https://example.com/images/yamazaki12.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 17,
                name = "白州 12年",
                kana = "はくしゅう 12ねん",
                mainCategory = "ウイスキー",
                subCategory = "シングルモルト",
                region = "山梨県",
                abv = 43,
                initialVolume = 700,
                remainingVolumePercent = 75,
                purchasePrice = 14000,
                notes = "森の香りと爽やかな味わい。",
                imageUrl = "https://example.com/images/hakushu12.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 18,
                name = "響 17年",
                kana = "ひびき 17ねん",
                mainCategory = "ウイスキー",
                subCategory = "ブレンデッド",
                region = "大阪府",
                abv = 43,
                initialVolume = 700,
                remainingVolumePercent = 25,
                purchasePrice = 80000,
                notes = "プレミアムブレンデッド。調和の取れた味わい。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 19,
                name = "竹鶴 ピュアモルト",
                kana = "たけつる ピュアモルト",
                mainCategory = "ウイスキー",
                subCategory = "ピュアモルト",
                region = "広島県",
                abv = 43,
                initialVolume = 700,
                remainingVolumePercent = 100,
                purchasePrice = 5000,
                notes = "竹鶴政孝の名を冠したウイスキー。",
                imageUrl = "https://example.com/images/taketsuru.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 20,
                name = "余市 シングルモルト",
                kana = "よいち シングルモルト",
                mainCategory = "ウイスキー",
                subCategory = "シングルモルト",
                region = "北海道",
                abv = 45,
                initialVolume = 700,
                remainingVolumePercent = 50,
                purchasePrice = 8000,
                notes = "力強くスモーキー。北海道の風土を感じる。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 21,
                name = "宮城峡 シングルモルト",
                kana = "みやぎきょう シングルモルト",
                mainCategory = "ウイスキー",
                subCategory = "シングルモルト",
                region = "宮城県",
                abv = 45,
                initialVolume = 700,
                remainingVolumePercent = 75,
                purchasePrice = 7500,
                notes = "華やかでフルーティー。女性にも人気。",
                imageUrl = "https://example.com/images/miyagikyo.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 22,
                name = "知多",
                kana = "ちた",
                mainCategory = "ウイスキー",
                subCategory = "グレーン",
                region = "愛知県",
                abv = 43,
                initialVolume = 700,
                remainingVolumePercent = 100,
                purchasePrice = 3500,
                notes = "軽やかでスムース。ハイボールに最適。",
                imageUrl = "https://example.com/images/chita.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 23,
                name = "富士山麓 シグニチャーブレンド",
                kana = "ふじさんろく シグニチャーブレンド",
                mainCategory = "ウイスキー",
                subCategory = "ブレンデッド",
                region = "静岡県",
                abv = 50,
                initialVolume = 700,
                remainingVolumePercent = 50,
                purchasePrice = 4000,
                notes = "高アルコール度数。ロックで楽しむ。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 24,
                name = "イチローズモルト ワインウッドリザーブ",
                kana = "イチローズモルト ワインウッドリザーブ",
                mainCategory = "ウイスキー",
                subCategory = "シングルモルト",
                region = "埼玉県",
                abv = 46,
                initialVolume = 700,
                remainingVolumePercent = 25,
                purchasePrice = 12000,
                notes = "ワイン樽熟成。個性的な味わい。",
                imageUrl = "https://example.com/images/ichiros.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 25,
                name = "マルス ツインアルプス",
                kana = "マルス ツインアルプス",
                mainCategory = "ウイスキー",
                subCategory = "ブレンデッド",
                region = "長野県",
                abv = 40,
                initialVolume = 700,
                remainingVolumePercent = 100,
                purchasePrice = 2500,
                notes = "コスパ良し。デイリーに最適。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),

            // ワイン(8件)
            StockDto(
                id = 26,
                name = "シャトー・メルシャン 桔梗ヶ原メルロー",
                kana = "シャトー・メルシャン ききょうがはらメルロー",
                mainCategory = "ワイン",
                subCategory = "赤ワイン",
                region = "長野県",
                abv = 12,
                initialVolume = 750,
                remainingVolumePercent = 75,
                purchasePrice = 5000,
                notes = "日本ワインの代表格。エレガントで繊細。",
                imageUrl = "https://example.com/images/chateaumercian.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 27,
                name = "登美の丘 甲州",
                kana = "とみのおか こうしゅう",
                mainCategory = "ワイン",
                subCategory = "白ワイン",
                region = "山梨県",
                abv = 12,
                initialVolume = 750,
                remainingVolumePercent = 50,
                purchasePrice = 3500,
                notes = "甲州種100%。和食との相性が良い。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 28,
                name = "岩の原葡萄園 マスカット・ベーリーA",
                kana = "いわのはらぶどうえん マスカット・ベーリーA",
                mainCategory = "ワイン",
                subCategory = "赤ワイン",
                region = "新潟県",
                abv = 12,
                initialVolume = 750,
                remainingVolumePercent = 100,
                purchasePrice = 2800,
                notes = "日本固有品種。フルーティーで飲みやすい。",
                imageUrl = "https://example.com/images/iwanohara.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 29,
                name = "五一わいん メルロー",
                kana = "ごいちワイン メルロー",
                mainCategory = "ワイン",
                subCategory = "赤ワイン",
                region = "長野県",
                abv = 12,
                initialVolume = 720,
                remainingVolumePercent = 25,
                purchasePrice = 2000,
                notes = "コスパの良い国産ワイン。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 30,
                name = "余市ケルナー",
                kana = "よいちケルナー",
                mainCategory = "ワイン",
                subCategory = "白ワイン",
                region = "北海道",
                abv = 11,
                initialVolume = 750,
                remainingVolumePercent = 50,
                purchasePrice = 3000,
                notes = "北海道ワイン。爽やかな酸味が特徴。",
                imageUrl = "https://example.com/images/yoichikerner.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 31,
                name = "タケダワイナリー サン・スフル",
                kana = "タケダワイナリー サン・スフル",
                mainCategory = "ワイン",
                subCategory = "白ワイン",
                region = "山形県",
                abv = 12,
                initialVolume = 750,
                remainingVolumePercent = 75,
                purchasePrice = 2500,
                notes = "無添加ワイン。自然派の味わい。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 32,
                name = "ココ・ファーム こころみ",
                kana = "ココ・ファーム こころみ",
                mainCategory = "ワイン",
                subCategory = "オレンジワイン",
                region = "栃木県",
                abv = 12,
                initialVolume = 750,
                remainingVolumePercent = 100,
                purchasePrice = 4000,
                notes = "個性的なオレンジワイン。",
                imageUrl = "https://example.com/images/cocofarm.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 33,
                name = "グレイス甲州 鳥居平畑",
                kana = "グレイスこうしゅう とりいだいらばたけ",
                mainCategory = "ワイン",
                subCategory = "白ワイン",
                region = "山梨県",
                abv = 12,
                initialVolume = 750,
                remainingVolumePercent = 50,
                purchasePrice = 8000,
                notes = "プレミアム甲州ワイン。ミネラル感が豊富。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),

            // ビール(5件)
            StockDto(
                id = 34,
                name = "よなよなエール",
                kana = "よなよなエール",
                mainCategory = "ビール",
                subCategory = "エール",
                region = "長野県",
                abv = 5,
                initialVolume = 350,
                remainingVolumePercent = 100,
                purchasePrice = 300,
                notes = "ホップの香りが強いクラフトビール。",
                imageUrl = "https://example.com/images/yonayona.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 35,
                name = "水曜日のネコ",
                kana = "すいようびのネコ",
                mainCategory = "ビール",
                subCategory = "ベルジャンホワイト",
                region = "長野県",
                abv = 5,
                initialVolume = 350,
                remainingVolumePercent = 75,
                purchasePrice = 320,
                notes = "爽やかで飲みやすい白ビール。",
                imageUrl = "https://example.com/images/suiyouneko.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 36,
                name = "インドの青鬼",
                kana = "インドのあおおに",
                mainCategory = "ビール",
                subCategory = "IPA",
                region = "長野県",
                abv = 7,
                initialVolume = 350,
                remainingVolumePercent = 50,
                purchasePrice = 350,
                notes = "苦味の強いIPA。ホップ好きにおすすめ。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 37,
                name = "常陸野ネストビール ホワイトエール",
                kana = "ひたちのネストビール ホワイトエール",
                mainCategory = "ビール",
                subCategory = "ホワイトエール",
                region = "茨城県",
                abv = 5,
                initialVolume = 330,
                remainingVolumePercent = 100,
                purchasePrice = 400,
                notes = "世界的に有名な日本のクラフトビール。",
                imageUrl = "https://example.com/images/hitachino.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 38,
                name = "ザ・プレミアム・モルツ",
                kana = "ザ・プレミアム・モルツ",
                mainCategory = "ビール",
                subCategory = "ラガー",
                region = "東京都",
                abv = 5,
                initialVolume = 350,
                remainingVolumePercent = 75,
                purchasePrice = 250,
                notes = "サントリーのプレミアムビール。華やかな香り。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),

            // 焼酎(5件)
            StockDto(
                id = 39,
                name = "魔王",
                kana = "まおう",
                mainCategory = "焼酎",
                subCategory = "芋焼酎",
                region = "鹿児島県",
                abv = 25,
                initialVolume = 1800,
                remainingVolumePercent = 50,
                purchasePrice = 8000,
                notes = "プレミアム芋焼酎。フルーティーで飲みやすい。",
                imageUrl = "https://example.com/images/maou.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 40,
                name = "森伊蔵",
                kana = "もりいぞう",
                mainCategory = "焼酎",
                subCategory = "芋焼酎",
                region = "鹿児島県",
                abv = 25,
                initialVolume = 1800,
                remainingVolumePercent = 25,
                purchasePrice = 12000,
                notes = "3Mの一つ。入手困難な銘柄。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 41,
                name = "村尾",
                kana = "むらお",
                mainCategory = "焼酎",
                subCategory = "芋焼酎",
                region = "鹿児島県",
                abv = 25,
                initialVolume = 1800,
                remainingVolumePercent = 75,
                purchasePrice = 10000,
                notes = "3Mの一つ。まろやかで深い味わい。",
                imageUrl = "https://example.com/images/murao.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 42,
                name = "佐藤 黒",
                kana = "さとう くろ",
                mainCategory = "焼酎",
                subCategory = "芋焼酎",
                region = "鹿児島県",
                abv = 25,
                initialVolume = 1800,
                remainingVolumePercent = 50,
                purchasePrice = 6000,
                notes = "黒麹仕込み。コクがある。",
                imageUrl = null,
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            ),
            StockDto(
                id = 43,
                name = "いいちこ",
                kana = "いいちこ",
                mainCategory = "焼酎",
                subCategory = "麦焼酎",
                region = "大分県",
                abv = 25,
                initialVolume = 1800,
                remainingVolumePercent = 100,
                purchasePrice = 2000,
                notes = "定番の麦焼酎。クセがなく飲みやすい。",
                imageUrl = "https://example.com/images/iichiko.jpg",
                createdAt = baseTime.toString(),
                updatedAt = baseTime.toString()
            )
        )
    }
}
