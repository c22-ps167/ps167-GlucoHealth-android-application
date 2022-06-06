package com.example.glucohealth.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glucohealth.R
import com.example.glucohealth.adapter.SearchRVAdapter
import com.example.glucohealth.databinding.FragmentSearchBinding
import com.example.glucohealth.response.DataItem
import com.example.glucohealth.viewmodels.ProductViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.show()

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]
        generateSearchItem()

        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                generateResponse(binding.edtSearch.text.toString())
                true
            } else {
                false
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateSearchItem(){
        viewModel.searchResponse.observe(viewLifecycleOwner) { search ->
            setSearchData(search)
        }
        viewModel.isFailing.observe(this, {
            failing(it)
        })
        viewModel.isNotFound.observe(this, {
            notFound(it)
        })
        viewModel.isloading.observe(this, {
            showLoading(it)
        })
    }

    private fun generateResponse(query: String){
        viewModel.getSearchResponse(query)
        generateSearchItem()
    }

    private fun setSearchData(searchRes: List<DataItem>){
        val listProduct = ArrayList<DataItem>()
        val listSearchAdapter = SearchRVAdapter()
        listProduct.addAll(searchRes)
        listSearchAdapter.setList(listProduct)
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listSearchAdapter
        }
        listSearchAdapter.setOnItemClickCallback(object : SearchRVAdapter.OnItemClickCallback{
            override fun onItemClicked(data: DataItem) {
                Toast.makeText(layoutInflater.context, data.name.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun notFound(isNotFound: Boolean){
        if(isNotFound){
            binding.tvMessage.text = getString(R.string.notfound)
            binding.tvMessage.visibility = View.VISIBLE
        }else{
            binding.tvMessage.visibility = View.GONE
        }
    }

//    private fun showSelectedData(user: DataItem) {
//        val toUserDetail = Intent(context, UserActivity::class.java)
//            .putExtra(UserActivity.EXRA_USERS, user.login)
//        startActivity(toUserDetail)
//    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbSearch.visibility = View.VISIBLE
        } else {
            binding.pbSearch.visibility = View.GONE
        }
    }

    private fun failing(isFail: Boolean){
        if(isFail) {
            Toast.makeText(context, getString(R.string.failtoshowdata), Toast.LENGTH_SHORT).show()
        }
    }

}