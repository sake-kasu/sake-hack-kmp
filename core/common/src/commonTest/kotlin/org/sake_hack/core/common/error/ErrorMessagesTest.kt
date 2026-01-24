package org.sake_hack.core.common.error

import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorMessagesTest {

    @Test
    fun `Network timeout returns user friendly message`() {
        val error = AppError.Network(
            errorMessage = "timeout",
            errorCause = null,
            errorType = NetworkErrorType.TIMEOUT
        )

        assertEquals("通信がタイムアウトしました", error.toUserMessage())
    }

    @Test
    fun `Network connection failed returns user friendly message`() {
        val error = AppError.Network(
            errorMessage = "connection failed",
            errorCause = null,
            errorType = NetworkErrorType.CONNECTION_FAILED
        )

        assertEquals("サーバーに接続できませんでした", error.toUserMessage())
    }

    @Test
    fun `HTTP 404 returns user friendly message`() {
        val error = AppError.Http(
            statusCode = 404,
            errorMessage = "Not found",
            errorCause = null
        )

        assertEquals("データが見つかりませんでした", error.toUserMessage())
    }

    @Test
    fun `HTTP 500 returns user friendly message`() {
        val error = AppError.Http(
            statusCode = 500,
            errorMessage = "Server error",
            errorCause = null
        )

        assertEquals("サーバーエラーが発生しました", error.toUserMessage())
    }

    @Test
    fun `Parse error returns user friendly message`() {
        val error = AppError.Parse(
            errorMessage = "parse error",
            errorCause = null
        )

        assertEquals("データの読み込みに失敗しました", error.toUserMessage())
    }

    @Test
    fun `Unknown error returns user friendly message`() {
        val error = AppError.Unknown(
            errorMessage = "unknown",
            errorCause = null
        )

        assertEquals("予期しないエラーが発生しました", error.toUserMessage())
    }

    @Test
    fun `HTTP 500 is retryable`() {
        val error = AppError.Http(
            statusCode = 500,
            errorMessage = "Server error",
            errorCause = null
        )

        assertEquals(true, error.isRetryable)
    }

    @Test
    fun `HTTP 404 is not retryable`() {
        val error = AppError.Http(
            statusCode = 404,
            errorMessage = "Not found",
            errorCause = null
        )

        assertEquals(false, error.isRetryable)
    }
}
