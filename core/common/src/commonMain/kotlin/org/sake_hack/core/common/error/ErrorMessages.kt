package org.sake_hack.core.common.error

/**
 * AppErrorをユーザー向けメッセージに変換
 *
 * テクニカルなエラー詳細を隠し、ユーザーフレンドリーな日本語メッセージを返す。
 * Presentation層のViewModelで使用される。
 */
fun AppError.toUserMessage(): String {
    return when (this) {
        is AppError.Network -> when (errorType) {
            NetworkErrorType.CONNECTION_FAILED -> "サーバーに接続できませんでした"
            NetworkErrorType.TIMEOUT -> "通信がタイムアウトしました"
            NetworkErrorType.NO_INTERNET -> "インターネット接続を確認してください"
            NetworkErrorType.UNKNOWN -> "ネットワークエラーが発生しました"
        }

        is AppError.Http -> when (statusCode) {
            400 -> "リクエストが正しくありません"
            401 -> "認証が必要です"
            403 -> "アクセスが許可されていません"
            404 -> "データが見つかりませんでした"
            500 -> "サーバーエラーが発生しました"
            503 -> "サービスが一時的に利用できません"
            else -> "エラーが発生しました (${statusCode})"
        }

        is AppError.Api -> "エラー: $message"
        is AppError.Parse -> "データの読み込みに失敗しました"
        is AppError.Unknown -> "予期しないエラーが発生しました"
    }
}
