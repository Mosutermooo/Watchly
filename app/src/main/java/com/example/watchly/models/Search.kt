package com.example.watchly.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "searchTable")
data class Search (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val query: String? = null,
    val searchedOnDay: String? = null
        ) : Serializable