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
import com.example.glucohealth.ui.activity.ProductDetailActivity
import com.example.glucohealth.viewmodels.FavViewModel

class FavoriteFragment : Fragment() {
    private var _binding : FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        viewModel = obtainViewModel(requireActivity())
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
                showSelectedProduct(data)
            }

        })
    }

    private fun showSelectedProduct(product: FavEntity){
        val toProductDetail = Intent(layoutInflater.context, ProductDetailActivity::class.java)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCTNAME, product.productName)
            .putExtra(ProductDetailActivity.EXTRA_IMGURL, product.imgUrl)
            .putExtra(ProductDetailActivity.EXTRA_CALORIES, product.calories)
            .putExtra(ProductDetailActivity.EXTRA_PROTEIN,product.protein)
            .putExtra(ProductDetailActivity.EXTRA_FAT, product.totalFat)
            .putExtra(ProductDetailActivity.EXTRA_SERVINGSIZE, product.servingSize)
            .putExtra(ProductDetailActivity.EXTRA_SODIUM, product.sodium)
            .putExtra(ProductDetailActivity.EXTRA_SUGAR, product.sugar)
            .putExtra(ProductDetailActivity.EXTRA_CARBO, product.totalCarbohydrate)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCTID, product.productId)
            .putExtra(ProductDetailActivity.EXTRA_SATFAT, product.saturatedFat)
        startActivity(toProductDetail)
    }

    private fun obtainViewModel(activity: FragmentActivity): FavViewModel{
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavViewModel::class.java]
    }
}