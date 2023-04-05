package com.appify.secretwindow.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.appify.secretwindow.dataModels.SecretItem

@Database(
    entities = [SecretItem::class],
    version = 1,
    exportSchema = false
)

abstract class SecretItemsDatabase: RoomDatabase() {
    abstract fun getDatabaseDAO(): DatabaseDAO
    companion object {
        @Volatile
        private var databaseInstance: SecretItemsDatabase? = null
        private var databaseLock = Any()

        fun getInstance(): SecretItemsDatabase? = databaseInstance

        operator fun invoke(currContext: Context) = databaseInstance ?: synchronized((databaseLock)) {
            databaseInstance ?: createDatabase(currContext).also {
                databaseInstance = it
            }
        }

        private fun createDatabase(currContext: Context) =
            Room.databaseBuilder(
                currContext.applicationContext,
                SecretItemsDatabase::class.java,
                "secrets_db.db"
            ).build()
    }
}