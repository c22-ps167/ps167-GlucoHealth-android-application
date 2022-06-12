package com.example.glucohealth.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.glucohealth.database.entity.SugarEntity

@Database(entities = [SugarEntity::class], version = 1)
abstract class SugarDatabase: RoomDatabase() {
    abstract fun sugarDao(): SugarDao

    companion object{
        @Volatile
        private var INSTANCE: SugarDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): SugarDatabase{
            if(INSTANCE == null){
                synchronized(SugarDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    SugarDatabase::class.java, "sugar_db").build()
                }
            }
            return INSTANCE as SugarDatabase
        }
    }
}