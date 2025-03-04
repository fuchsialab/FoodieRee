package com.fuchsia.foodieree.data.repo

import com.fuchsia.foodieree.data.room.Dao
import com.fuchsia.foodieree.data.room.FoodEntity
import javax.inject.Inject

class RoomRepo @Inject constructor(
    private val dao: Dao
){

    suspend fun getAllFood(): List<FoodEntity> {
        return dao.getAllFood()

    }

    suspend fun insertFood(foodEntity: FoodEntity) {
        dao.insertFood(foodEntity)
    }

    suspend fun deleteFood(foodEntity: FoodEntity) {
        dao.deleteFood(foodEntity)
    }

}