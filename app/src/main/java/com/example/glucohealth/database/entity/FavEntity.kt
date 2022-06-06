package com.example.glucohealth.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "fav_product")
class FavEntity(
    @field:ColumnInfo(name = "productId")
    @field:PrimaryKey
    val productId: String,

    @field:ColumnInfo(name = "ProductName")
    val productName: String,

    @field:ColumnInfo(name = "imgUrl")
    val imgUrl: String
): Parcelable