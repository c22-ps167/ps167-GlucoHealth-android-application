package com.example.glucohealth.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("status")
	val status: String
)

data class ProductDetail(
	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: DataItem,

	@field:SerializedName("status")
	val status: String
)

data class DataItem(

	@field:SerializedName("nutritionFact")
	val nutritionFact: NutritionFact,

	@field:SerializedName("createdAt")
	val createdAt: Long? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: Any? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class NutritionFact(

	@field:SerializedName("saturatedFat")
	val saturatedFat: Int? = null,

	@field:SerializedName("sodium")
	val sodium: Int? = null,

	@field:SerializedName("totalFat")
	val totalFat: Int? = null,

	@field:SerializedName("protein")
	val protein: Int? = null,

	@field:SerializedName("totalCarbohydrate")
	val totalCarbohydrate: Int? = null,

	@field:SerializedName("calories")
	val calories: Int? = null,

	@field:SerializedName("sugar")
	val sugar: Int? = null,

	@field:SerializedName("servingSize")
	val servingSize: Int? = null
)
