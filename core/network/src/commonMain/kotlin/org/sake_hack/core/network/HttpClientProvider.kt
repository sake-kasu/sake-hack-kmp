package org.sake_hack.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.sake_hack.core.common.error.AppError

/**
 * プラットフォーム固有のHTTPクライアント作成のためのexpect宣言
 */
expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

/**
 * アプリケーション用に設定されたHTTPクライアントを作成
 * プラットフォーム固有のエンジンにはexpect/actualパターンを使用
 */
fun createAppHttpClient() = httpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(Logging) {
        level = LogLevel.INFO
        logger = object : Logger {
            override fun log(message: String) {
                println("Ktor: $message")
            }
        }
    }

    HttpResponseValidator {
        validateResponse { response ->
            val statusCode = response.status.value

            when {
                statusCode >= 400 -> {
                    val responseBody = try {
                        response.bodyAsText()
                    } catch (e: Exception) {
                        null
                    }

                    throw AppError.Http(
                        statusCode = statusCode,
                        errorMessage = "HTTP $statusCode: ${response.status.description}",
                        errorCause = null,
                        responseBody = responseBody
                    )
                }
            }
        }

        handleResponseExceptionWithRequest { cause, _ ->
            if (cause is AppError) {
                throw cause
            }
        }
    }

//    defaultRequest {
        // ベースURLはエンドポイントごとに設定
        // url("https://api.example.com/")
//    }
}
