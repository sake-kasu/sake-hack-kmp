package org.sake_hack.feature.stocklist.domain.validation

import org.sake_hack.feature.stocklist.domain.model.StockEditField
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest

/**
 * 在庫バリデーター
 *
 * iOS側でも使用可能なバリデーションロジック
 */
object StockValidator {
    /** 記号チェック用正規表現(記号を許可しない) */
    private val SYMBOL_REGEX = Regex("^[^!@#$%^&*()_+=\\[\\]{};:'\",.<>?/\\\\|`~]*$")

    /** 大分類の選択肢 */
    val MAIN_CATEGORIES = listOf(
        "日本酒",
        "ウイスキー",
        "ワイン",
        "ビール",
        "焼酎",
        "リキュール",
        "その他"
    )

    /** 産地の選択肢(都道府県リスト) */
    val REGIONS = listOf(
        "北海道",
        "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
        "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
        "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県",
        "三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県",
        "鳥取県", "島根県", "岡山県", "広島県", "山口県",
        "徳島県", "香川県", "愛媛県", "高知県",
        "福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
    )

    /** 残容量の選択肢(25の倍数) */
    val REMAINING_VOLUME_OPTIONS = listOf(0, 25, 50, 75, 100)

    /**
     * 在庫編集リクエストをバリデーション
     *
     * @param request 在庫編集リクエスト
     * @return バリデーションエラー(キー: フィールド名、値: エラーメッセージ)
     */
    fun validateStockEdit(request: StockEditRequest): Map<StockEditField, String> {
        val errors = mutableMapOf<StockEditField, String>()

        // 名前バリデーション
        when {
            request.name.isBlank() -> errors[StockEditField.NAME] = "名前を入力してください"
            request.name.length > 100 -> errors[StockEditField.NAME] = "名前は100文字以内で入力してください"
            !request.name.matches(SYMBOL_REGEX) -> errors[StockEditField.NAME] = "名前に記号を含めることはできません"
        }

        // ふりがなバリデーション
        when {
            request.kana.isBlank() -> errors[StockEditField.KANA] = "ふりがなを入力してください"
            request.kana.length > 100 -> errors[StockEditField.KANA] = "ふりがなは100文字以内で入力してください"
            !request.kana.matches(SYMBOL_REGEX) -> errors[StockEditField.KANA] = "ふりがなに記号を含めることはできません"
        }

        // 大分類バリデーション
        if (request.mainCategory.isBlank()) {
            errors[StockEditField.MAIN_CATEGORY] = "大分類を選択してください"
        }

        // 小分類バリデーション(オプショナル)
        request.subCategory?.let { subCategory ->
            when {
                subCategory.length > 100 -> errors[StockEditField.SUB_CATEGORY] = "小分類は100文字以内で入力してください"
                !subCategory.matches(SYMBOL_REGEX) -> errors[StockEditField.SUB_CATEGORY] = "小分類に記号を含めることはできません"
            }
        }

        // 度数バリデーション
        when {
            request.abv < 0 || request.abv > 100 -> errors[StockEditField.ABV] = "度数は0〜100の範囲で入力してください"
        }

        // 購入時容量バリデーション
        when {
            request.initialVolume < 0 || request.initialVolume > 10000 -> errors[StockEditField.INITIAL_VOLUME] =
                "購入時容量は0〜10000の範囲で入力してください"
        }

        // 残容量バリデーション
        if (!REMAINING_VOLUME_OPTIONS.contains(request.remainingVolumePercent)) {
            errors[StockEditField.REMAINING_VOLUME] = "残容量は0, 25, 50, 75, 100のいずれかを選択してください"
        }

        // 購入時価格バリデーション
        when {
            request.purchasePrice < 0 || request.purchasePrice > 1000000 -> errors[StockEditField.PURCHASE_PRICE] =
                "購入時価格は0〜1000000の範囲で入力してください"
        }

        // 自由記述バリデーション(オプショナル)
        request.notes?.let { notes ->
            if (notes.length > 500) {
                errors[StockEditField.NOTES] = "自由記述は500文字以内で入力してください"
            }
        }

        return errors
    }

    /**
     * 画像ファイルサイズをバリデーション
     *
     * @param imageData 画像データ
     * @return エラーメッセージ(正常な場合はnull)
     */
    fun validateImageSize(imageData: ByteArray): String? {
        val maxSizeBytes = 10 * 1024 * 1024 // 10MB
        return if (imageData.size > maxSizeBytes) {
            "画像ファイルは10MB以下にしてください"
        } else {
            null
        }
    }

    /**
     * 記号を含まないかチェック
     *
     * @param text チェック対象のテキスト
     * @return 記号を含まない場合true
     */
    fun hasNoSymbols(text: String): Boolean = text.matches(SYMBOL_REGEX)
}
