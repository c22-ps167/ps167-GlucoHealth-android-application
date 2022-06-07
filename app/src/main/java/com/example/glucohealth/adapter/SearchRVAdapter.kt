package com.example.glucohealth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glucohealth.databinding.ItemRowProductBinding
import com.example.glucohealth.response.DataItem

class SearchRVAdapter: RecyclerView.Adapter<SearchRVAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private val listProduct = ArrayList<DataItem>()

    class ViewHolder(var binding: ItemRowProductBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productList = listProduct[position]
        holder.binding.tvItemUsername.text = productList.name
        Glide.with(holder.itemView)
            .load(productList.url)
            .into(holder.binding.imgItemPhoto)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listProduct[holder.adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = listProduct.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setList(user: ArrayList<DataItem>){
        listProduct.clear()
        listProduct.addAll(user)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataItem)
    }

}