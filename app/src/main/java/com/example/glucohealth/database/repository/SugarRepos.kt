package com.example.glucohealth.database.repository

import android.app.Application
import com.example.glucohealth.database.entity.SugarEntity
import com.example.glucohealth.database.room.SugarDao
import com.example.glucohealth.database.room.SugarDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SugarRepos (application: Application){
    private val mSugarDao: SugarDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = SugarDatabase.getDatabase(application)
        mSugarDao = db.sugarDao()
    }

    fun getCount(time: String) = mSugarDao.getCount(time)

    fun getAllConsumtion(time: String) = mSugarDao.getAllConsumtion(time)

    fun insert(sugar: SugarEntity){
        executorService.execute{mSugarDao.insert(sugar)}
    }
}