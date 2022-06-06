package com.example.glucohealth.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.glucohealth.database.entity.FavEntity

@Database(entities = [FavEntity::class], version = 1)
abstract class FavDatabase: RoomDatabase() {
    abstract fun favProductDao(): FavDao

    companion object{
        @Volatile
        private var INSTANCE: FavDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): FavDatabase {
            if (INSTANCE == null) {
                synchronized(FavDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        FavDatabase::class.java, "fav_product")
                        .build()
                }
            }
            return INSTANCE as FavDatabase
        }
    }

}