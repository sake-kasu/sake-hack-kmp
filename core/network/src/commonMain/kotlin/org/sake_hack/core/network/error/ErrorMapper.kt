package org.sake_hack.core.network.error

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.SerializationException
import org.sake_hack.core.common.error.AppError
import org.sake_hack.core.common.error.NetworkErrorType

/**
 * Ktor例外をAppErrorに変換
 *
 * Data層のRepositoryで使用され、プラットフォーム固有の例外を
 * ドメイン層のAppErrorに変換する。
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is ConnectTimeoutException, is SocketTimeoutException, is HttpRequestTimeoutException ->
            AppError.Network(
                errorMessage = "Timeout",
                errorCause = this,
                errorType = NetworkErrorType.TIMEOUT
            )

        is SerializationException, is JsonConvertException -> AppError.Parse(
            errorMessage = "Parse error",
            errorCause = this
        )

        else -> AppError.Unknown(
            errorMessage = this.message ?: "Unknown error",
            errorCause = this
        )
    }
}
