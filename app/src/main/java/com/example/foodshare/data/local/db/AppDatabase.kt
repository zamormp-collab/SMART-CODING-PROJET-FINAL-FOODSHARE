package com.example.foodshare.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OffreEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun offreDao(): OffreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodshare_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
