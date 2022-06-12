package com.example.glucohealth.api_service

import com.example.glucohealth.BuildConfig
import com.example.glucohealth.response.PredictItem
import com.example.glucohealth.response.ProductDetail
import com.example.glucohealth.response.SearchResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/api/p/{id}")
    @Headers(BuildConfig.APIKEY)
    fun getProduct(
        @Path("id") id: String
    ): Call<ProductDetail>

    @GET("/api/p")
    @Headers(BuildConfig.APIKEY)
    fun searchProduct(
        @Query(value = "name") name: String,
        @Query(value = "page") page: Int? = 1,
        @Query(value = "size") size: Int? = 10
    ): Call<SearchResponse>

    @POST("predict")
    @Multipart
    fun getPrediction(
        @Part file: MultipartBody.Part
    ): Call<PredictItem>

}