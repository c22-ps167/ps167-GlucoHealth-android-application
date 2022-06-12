package com.example.glucohealth.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.glucohealth.database.entity.SugarEntity
import com.example.glucohealth.database.repository.SugarRepos

class SugarViewModel(application: Application) : ViewModel() {
    private val mSugarRepos: SugarRepos = SugarRepos(application)

    fun insert(product: SugarEntity){
        mSugarRepos.insert(product)
    }

    fun getAllProduct(time: String): LiveData<List<SugarEntity>> = mSugarRepos.getAllConsumtion(time)

    fun count(time: String) = mSugarRepos.getCount(time)

}