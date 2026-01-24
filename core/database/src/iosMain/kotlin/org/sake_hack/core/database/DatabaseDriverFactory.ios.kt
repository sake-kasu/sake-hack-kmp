package org.sake_hack.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS固有のデータベースドライバー実装
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SakeDatabase.Schema,
            name = "sake.db"
        )
    }
}
