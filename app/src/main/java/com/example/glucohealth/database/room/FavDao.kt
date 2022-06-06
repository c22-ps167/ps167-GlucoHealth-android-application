package com.example.glucohealth.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.glucohealth.database.entity.FavEntity

@Dao
interface FavDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fav: FavEntity)

    @Query("DELETE FROM fav_product WHERE productId = :productId")
    fun delete(productId: String)

    @Query("SELECT * from fav_product")
    fun getAllProduct(): LiveData<List<FavEntity>>

    @Query("SELECT count(*) FROM fav_product WHERE productId = :productId")
    suspend fun favorite(productId: String): Int
}