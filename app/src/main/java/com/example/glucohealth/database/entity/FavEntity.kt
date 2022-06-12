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
    val imgUrl: String,

    @field:ColumnInfo(name ="saturatedFat")
    val saturatedFat: Int,

    @field:ColumnInfo(name ="sodium")
    val sodium: Int,

    @field:ColumnInfo(name ="totalFat")
    val totalFat: Int,

    @field:ColumnInfo(name ="protein")
    val protein: Int,

    @field:ColumnInfo(name ="totalCarbohydrate")
    val totalCarbohydrate: Int,

    @field:ColumnInfo(name ="calories")
    val calories: Int,

    @field:ColumnInfo(name ="sugar")
    val sugar: Int,

    @field:ColumnInfo(name ="servingSize")
    val servingSize: Int
): Parcelable