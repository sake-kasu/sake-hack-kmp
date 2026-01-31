package org.sake_hack.core.database

import app.cash.sqldelight.db.SqlDriver

/**
 * プラットフォーム固有のデータベースドライバー作成のためのexpect宣言
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
