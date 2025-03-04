package com.fuchsia.foodieree.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val location: String,
    val videoUrl: String
)