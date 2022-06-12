package com.example.glucohealth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glucohealth.database.entity.FavEntity
import com.example.glucohealth.databinding.ItemRowProductBinding

class FavRvAdapter: RecyclerView.Adapter<FavRvAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private val listProduct = ArrayList<FavEntity>()
    class ViewHolder(var binding: ItemRowProductBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productList = listProduct[position]
        holder.binding.tvItemProductname.text = productList.productName
        Glide.with(holder.itemView)
            .load(productList.imgUrl)
            .into(holder.binding.imgItemPhoto)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listProduct[holder.adapterPosition]) }
    }

    override fun getItemCount() = listProduct.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    fun setList(product: ArrayList<FavEntity>){
        listProduct.clear()
        listProduct.addAll(product)
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: FavEntity)
    }
}