package com.fuchsia.foodieree.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.fuchsia.foodieree.data.repo.LocationRepo
import com.fuchsia.foodieree.data.repo.RoomRepo
import com.fuchsia.foodieree.data.room.AppDatabase
import com.fuchsia.foodieree.data.room.Dao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DiModule {

    @Provides
    @Singleton
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepo {
        return LocationRepo(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "food_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: AppDatabase): Dao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideRoomRepository(dao: Dao): RoomRepo {
        return RoomRepo(dao)
    }


}