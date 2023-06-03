package com.fimo.aidentist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fimo.aidentist.databinding.ItemRowHistoryBinding

class ImageAdapter(private val images: List<Pair<String, String>>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private var onItemClickListener: ((Pair<String, String>) -> Unit)? = null

    fun setOnItemClickListener(listener: (Pair<String, String>) -> Unit) {
        onItemClickListener = listener
    }

    class ViewHolder(var binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, url) = images[position]
        with(holder.binding) {
            Glide.with(root.context)
                .load(url)
                .into(imgItemPhoto)
            tvTittle.text = name
            root.setOnClickListener {
                onItemClickListener?.invoke(images[position])
            }
        }
    }

    override fun getItemCount(): Int = images.size
}