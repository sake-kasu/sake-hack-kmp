package org.sake_hack.core.common.error

/**
 * アプリケーション全体で使用する統一エラー型
 *
 * 100本のAPI対応に向けた統一エラーハンドリング基盤。
 * Data層でKtor例外から変換され、Presentation層でユーザー向けメッセージに変換される。
 */
sealed class AppError(
    override val message: String,
    override val cause: Throwable?
) : Throwable(message, cause) {
    abstract val isRetryable: Boolean

    /**
     * ネットワークエラー (接続失敗、タイムアウト)
     */
    data class Network(
        val errorMessage: String,
        val errorCause: Throwable?,
        val errorType: NetworkErrorType
    ) : AppError(errorMessage, errorCause) {
        override val isRetryable: Boolean = true
    }

    /**
     * HTTPエラー (4xx, 5xx)
     */
    data class Http(
        val statusCode: Int,
        val errorMessage: String,
        val errorCause: Throwable?,
        val responseBody: String? = null
    ) : AppError(errorMessage, errorCause) {
        override val isRetryable: Boolean = statusCode >= 500
    }

    /**
     * APIビジネスロジックエラー
     */
    data class Api(
        val errorCode: String,
        val errorMessage: String,
        val errorCause: Throwable?
    ) : AppError(errorMessage, errorCause) {
        override val isRetryable: Boolean = false
    }

    /**
     * データパースエラー
     */
    data class Parse(
        val errorMessage: String,
        val errorCause: Throwable?
    ) : AppError(errorMessage, errorCause) {
        override val isRetryable: Boolean = false
    }

    /**
     * 未知のエラー
     */
    data class Unknown(
        val errorMessage: String,
        val errorCause: Throwable?
    ) : AppError(errorMessage, errorCause) {
        override val isRetryable: Boolean = true
    }
}

/**
 * ネットワークエラーの種類
 */
enum class NetworkErrorType {
    CONNECTION_FAILED,
    TIMEOUT,
    NO_INTERNET,
    UNKNOWN
}
