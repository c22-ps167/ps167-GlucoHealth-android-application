package com.example.glucohealth.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.glucohealth.database.entity.SugarEntity

@Dao
interface SugarDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(sugar: SugarEntity)

    @Query("SELECT * from daily_sugar WHERE time = :times ORDER BY number")
    fun getAllConsumtion(times: String): LiveData<List<SugarEntity>>
}