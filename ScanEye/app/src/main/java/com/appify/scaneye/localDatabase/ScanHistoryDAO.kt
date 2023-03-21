package com.appify.scaneye.localDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appify.scaneye.dataModels.ScanHistoryItem

@Dao
interface ScanHistoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewScan(givenScan: ScanHistoryItem): Long

    @Query("SELECT * FROM HistoryItemTable")
    fun getAllScanHistory(): LiveData<List<ScanHistoryItem>>
}