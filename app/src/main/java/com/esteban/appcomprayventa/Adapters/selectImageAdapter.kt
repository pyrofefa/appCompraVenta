package com.esteban.appcomprayventa.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.esteban.appcomprayventa.Models.SelectImageModel
import com.esteban.appcomprayventa.R
import com.esteban.appcomprayventa.databinding.ItemsImagesBinding
import java.lang.Exception

class selectImageAdapter(
    private val context : Context,
    private val imageSelectedArrayList : ArrayList<SelectImageModel>
): Adapter<selectImageAdapter.HolderImageSelected>() {

    private lateinit var binding: ItemsImagesBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImageSelected {
        binding = ItemsImagesBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImageSelected(binding.root)
    }

    override fun getItemCount(): Int {
        return imageSelectedArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImageSelected, position: Int) {
        val model = imageSelectedArrayList[position]

        val imageUri = model.imageUri
        try {
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.item_imagen)
                .into(holder.itemImage)
        }catch (e: Exception){

        }

        holder.btnClose.setOnClickListener {
            imageSelectedArrayList.remove(model)
            notifyDataSetChanged()
        }
    }

    inner class HolderImageSelected(itemView: View) : ViewHolder(itemView){
        var itemImage = binding.itemImage
        var btnClose = binding.closeItem
    }
}