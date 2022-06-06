package com.example.glucohealth.api_service

import com.example.glucohealth.BuildConfig
import com.example.glucohealth.response.ProductDetail
import com.example.glucohealth.response.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

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

}