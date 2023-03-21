package com.appify.scaneye.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.appify.scaneye.dataModels.ScanHistoryItem

@Database(
    entities = [ScanHistoryItem::class],
    version = 1,
    exportSchema = false
)
abstract class ScanHistoryDatabase : RoomDatabase() {
    abstract fun getScanHistoryDAO(): ScanHistoryDAO

    companion object {
        @Volatile
        private var databaseInstance: ScanHistoryDatabase? = null
        private val myLock = Any()

        operator fun invoke(currContext: Context) = databaseInstance ?: synchronized((myLock)) {
            databaseInstance ?: createDatabase(currContext)
        }

        private fun createDatabase(currContext: Context) =
            Room.databaseBuilder(
                currContext.applicationContext,
                ScanHistoryDatabase::class.java,
                "scanHistory_db.db"
            ).build()
    }
}