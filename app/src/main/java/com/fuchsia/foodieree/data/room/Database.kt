package com.fuchsia.foodieree.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract val dao: Dao

}