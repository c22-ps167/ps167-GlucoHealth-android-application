package com.example.glucohealth.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "daily_sugar")
class SugarEntity (
    @PrimaryKey
    @field:ColumnInfo(name = "number")
    val number: String,

    @field:ColumnInfo(name = "time")
    val time: String,

    @field:ColumnInfo(name = "productId")
    val productId: String,

    @field:ColumnInfo(name = "productName")
    val productName: String,

    @field:ColumnInfo(name = "imgUrl")
    val imgUrl: String,

    @field:ColumnInfo(name = "sugar")
    val sugar: Int

): Parcelable