package com.example.testyoutube.ui.youtubescreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.databinding.ContentListItemBinding

class VerticalListAdapter : RecyclerView.Adapter<VerticalListAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var listVideo: List<ItemVideo> = emptyList()
    private var currentId: Long = 0

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ContentListItemBinding.bind(itemView)

        fun bind(item: ItemVideo, currentId: Long) {
            binding.item = item
            binding.current = currentId
        }
        fun getBinding(): ContentListItemBinding = binding
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_list_item,parent,false)
        val holder = ViewHolder(view)
        val binding = holder.getBinding()
        binding.container.setOnClickListener {
            itemClick(holder.adapterPosition, binding)
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemClick(position: Int, binding: ContentListItemBinding) {
        val current = listVideo[position]
        currentId = current.id
        binding.current = currentId
        notifyDataSetChanged()
        listener?.onItemClick(current)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    interface OnItemClickListener {
        fun onItemClick(current: ItemVideo)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listVideo[position], currentId)
    }

    override fun getItemCount(): Int = listVideo.size
    override fun getItemId(position: Int): Long = listVideo[position].id

    fun getItemPosition(item: ItemVideo?): Int = listVideo.indexOf(item)

    fun getItemFromPosition(position: Int): ItemVideo = listVideo[position]

    fun setCurrentId(current: Long ) {
        currentId = current
    }
    fun getCurrentId(): Long = currentId

    fun getPosition(item: ItemVideo?, bias: Int): Int {
        var position = getItemPosition(item) + bias
        if(position<0 || position+1>listVideo.size) {
            position = -1
        }
        return position
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<ItemVideo>) {
        listVideo = list
        notifyDataSetChanged()
    }

}