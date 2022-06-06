package com.example.glucohealth.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.glucohealth.database.entity.FavEntity
import com.example.glucohealth.database.repository.FavRepos

class FavViewModel(application: Application) : ViewModel() {
    private val mFavRepos: FavRepos = FavRepos(application)

    fun insert(fav: FavEntity){
        mFavRepos.insert(fav)
    }

    fun delete(productId: String){
        mFavRepos.delete(productId)
    }

    fun getAllUsers(): LiveData<List<FavEntity>> = mFavRepos.getAllProduct()

    suspend fun isFavorite(productId: String) = mFavRepos.favProduct(productId)

}