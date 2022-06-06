package com.example.glucohealth.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.glucohealth.database.entity.FavEntity
import com.example.glucohealth.database.room.FavDao
import com.example.glucohealth.database.room.FavDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavRepos(application: Application) {
    private val mFavDao: FavDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavDatabase.getDatabase(application)
        mFavDao = db.favProductDao()
    }

    suspend fun favProduct(productId: String) = mFavDao.favorite(productId)

    fun getAllProduct(): LiveData<List<FavEntity>> = mFavDao.getAllProduct()

    fun insert(fav: FavEntity){
        executorService.execute{mFavDao.insert(fav)}
    }

    fun delete(productId: String){
        executorService.execute{mFavDao.delete(productId)}
    }
}