package com.example.glucohealth.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.glucohealth.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object{
        const val EXTRA_PRODUCTNAME = "extra_productname"
    }
}