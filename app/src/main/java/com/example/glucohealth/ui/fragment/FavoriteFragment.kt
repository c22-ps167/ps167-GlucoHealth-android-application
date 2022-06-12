package com.example.glucohealth.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glucohealth.adapter.FavRvAdapter
import com.example.glucohealth.database.entity.FavEntity
import com.example.glucohealth.databinding.FragmentFavoriteBinding
import com.example.glucohealth.helper.ViewModelFactory
import com.example.glucohealth.response.DataItem
import com.example.glucohealth.ui.activity.ProductDetailActivity
import com.example.glucohealth.viewmodels.FavViewModel
import com.example.glucohealth.viewmodels.ProductViewModel

class FavoriteFragment : Fragment() {
    private var _binding : FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavViewModel
    private lateinit var viewModelApi: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        viewModel = obtainViewModel(requireActivity())
        viewModelApi = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]
        generateFavorite()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateFavorite(){
        viewModel.getAllProduct().observe(viewLifecycleOwner){ product ->
            setFavorite(product)
        }
    }

    private fun setFavorite(favRes: List<FavEntity>){
        val listProduct = ArrayList<FavEntity>()
        val listFavoriteAdapter = FavRvAdapter()
        listProduct.addAll(favRes)
        listFavoriteAdapter.setList(listProduct)
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listFavoriteAdapter
        }
        listFavoriteAdapter.setOnItemClickCallback(object : FavRvAdapter.OnItemClickCallback{
            override fun onItemClicked(data: FavEntity) {
                selecteditem(data.productId)
            }

        })
    }

    private fun selecteditem(productId: String){
        viewModelApi.getProductDetail(productId)
        viewModelApi.detailProduct.observe(viewLifecycleOwner){
            viewModelApi.detailProduct.removeObservers(viewLifecycleOwner)
            showSelectedProduct(it)
        }
    }

    private fun showSelectedProduct(product: DataItem){
        val productNutritionFact = product.nutritionFact
        val toProductDetail = Intent(layoutInflater.context, ProductDetailActivity::class.java)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCTNAME, product.name)
            .putExtra(ProductDetailActivity.EXTRA_IMGURL, product.url)
            .putExtra(ProductDetailActivity.EXTRA_CALORIES, productNutritionFact.calories)
            .putExtra(ProductDetailActivity.EXTRA_PROTEIN,productNutritionFact.protein)
            .putExtra(ProductDetailActivity.EXTRA_FAT, productNutritionFact.saturatedFat)
            .putExtra(ProductDetailActivity.EXTRA_SERVINGSIZE, productNutritionFact.servingSize)
            .putExtra(ProductDetailActivity.EXTRA_SODIUM, productNutritionFact.sodium)
            .putExtra(ProductDetailActivity.EXTRA_SUGAR, productNutritionFact.sugar)
            .putExtra(ProductDetailActivity.EXTRA_CARBO, productNutritionFact.totalCarbohydrate)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCTID, product.id)
        startActivity(toProductDetail)
    }

    private fun obtainViewModel(activity: FragmentActivity): FavViewModel{
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavViewModel::class.java]
    }
}