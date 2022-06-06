package com.example.glucohealth.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.glucohealth.api_service.ApiConfig
import com.example.glucohealth.response.SearchResponse
import com.example.glucohealth.response.DataItem
import com.example.glucohealth.response.ProductDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel: ViewModel() {

    private val _detailProduct = MutableLiveData<DataItem>()
    val detailProduct: LiveData<DataItem> = _detailProduct

    private val _searchResponse = MutableLiveData<List<DataItem>>()
    val searchResponse: LiveData<List<DataItem>> = _searchResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isloading: LiveData<Boolean> = _isLoading

    private val _isNotFound = MutableLiveData<Boolean>()
    val isNotFound: LiveData<Boolean> = _isNotFound

    private val _isFailing = MutableLiveData<Boolean>()
    val isFailing: LiveData<Boolean> = _isFailing

    fun getSearchResponse(query: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchProduct(query)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                _isLoading.value = false
                _isNotFound.value = false
                if(response.isSuccessful){
                    _isFailing.value = false
                    val responseBody = response.body()
                    if(responseBody != null){
                        if(responseBody.data.count() == 0){
                            _isNotFound.value = true
                        }
                        _searchResponse.value = responseBody.data
                    } else{
                        _isFailing.value = true
                        Log.e("ERROR!", "onFailure: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                _isLoading.value = false
                _isFailing.value = true
                Log.e("ERROR!", "onFailure: ${t.message}")
            }
        })
    }

    fun getProductDetail(query: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getProduct(query)
        client.enqueue(object : Callback<ProductDetail> {
            override fun onResponse(
                call: Call<ProductDetail>,
                response: Response<ProductDetail>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _detailProduct.value = responseBody.data
                } else {
                    Log.e("ERROR!", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ProductDetail>, t: Throwable) {
                _isLoading.value = false
                Log.e("ERROR!", "onFailure: ${t.message}")
            }
        })
    }
}