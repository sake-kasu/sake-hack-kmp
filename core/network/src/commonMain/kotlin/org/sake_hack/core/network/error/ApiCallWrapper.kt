package org.sake_hack.core.network.error

import org.sake_hack.core.common.error.AppError

/**
 * Repository実装用のAPI呼び出しラッパー
 *
 * 100本のAPIで再利用可能な統一エラーハンドリング。
 * runCatching { ... }の代わりにsafeApiCall { ... }を使用する。
 *
 * 使用例:
 * ```
 * override suspend fun getAllSake(): Result<List<Sake>> = safeApiCall {
 *     val response = apiService.fetchSakeList()
 *     response.map { it.toDomain() }
 * }
 * ```
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: AppError) {
        // HttpResponseValidatorから投げられたAppErrorはそのまま返す
        Result.failure(e)
    } catch (e: Exception) {
        // その他の例外はAppErrorに変換
        Result.failure(e.toAppError())
    }
}
