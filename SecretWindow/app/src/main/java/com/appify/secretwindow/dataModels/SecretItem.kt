package com.appify.secretwindow.dataModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "secretItemsTable"
)
data class SecretItem(
    @PrimaryKey
    val sourceName: String,
    val timestamp: String,
    val text: String
)
