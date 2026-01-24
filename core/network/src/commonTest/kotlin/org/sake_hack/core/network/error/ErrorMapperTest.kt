package org.sake_hack.core.network.error

import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.serialization.SerializationException
import org.sake_hack.core.common.error.AppError
import org.sake_hack.core.common.error.NetworkErrorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorMapperTest {

    @Test
    fun `ConnectTimeoutException maps to Network error`() {
        val exception = ConnectTimeoutException("timeout")
        val error = exception.toAppError()

        assertTrue(error is AppError.Network)
        assertEquals(NetworkErrorType.TIMEOUT, error.errorType)
        assertTrue(error.isRetryable)
    }

    @Test
    fun `SerializationException maps to Parse error`() {
        val exception = SerializationException("parse error")
        val error = exception.toAppError()

        assertTrue(error is AppError.Parse)
        assertTrue(!error.isRetryable)
    }

    @Test
    fun `Unknown exception maps to Unknown error`() {
        val exception = RuntimeException("unknown error")
        val error = exception.toAppError()

        assertTrue(error is AppError.Unknown)
        assertEquals("unknown error", error.errorMessage)
        assertTrue(error.isRetryable)
    }
}
