package com.fuchsia.foodieree.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface Dao {

    @Query("SELECT * FROM food_table")
    suspend fun getAllFood(): List<FoodEntity>

    @Upsert
    suspend fun insertFood(foodEntity: FoodEntity)

    @Delete
    suspend fun deleteFood(foodEntity: FoodEntity)


}