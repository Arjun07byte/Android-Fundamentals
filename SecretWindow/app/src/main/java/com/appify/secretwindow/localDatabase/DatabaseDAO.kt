package com.appify.secretwindow.localDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appify.secretwindow.dataModels.SecretItem

@Dao
interface DatabaseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewSecret(newSecret: SecretItem)

    @Query("SELECT * FROM secretItemsTable")
    fun getAllSecrets(): LiveData<List<SecretItem>>

    @Query("DELETE FROM secretItemsTable")
    fun clearAllSecrets()
}