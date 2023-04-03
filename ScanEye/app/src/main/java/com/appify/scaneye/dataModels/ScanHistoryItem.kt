package com.appify.scaneye.dataModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "HistoryItemTable"
)
data class ScanHistoryItem(
    @PrimaryKey
    val historyItemTime: String,
    val historyItemTitle: String,
    val historyItemDesc: String,
    val historyItemType: String,
    var scanDateKey: Int
)
