package com.example.testyoutube.ui.youtubescreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.R
import com.example.testyoutube.data.videolistitem.ItemVideo
import com.example.testyoutube.databinding.ChannelsListItemBinding

class HorisontalListAdapter : RecyclerView.Adapter<HorisontalListAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var listVideo: List<ItemVideo> = emptyList()
    private var currentId: Long = 0

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ChannelsListItemBinding.bind(itemView)

        fun bind(item: ItemVideo, currentId: Long) {
            binding.item = item
            binding.current = currentId
        }
        fun getBinding(): ChannelsListItemBinding = binding
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.channels_list_item,parent,false)
        val holder = ViewHolder(view)
        val binding = holder.getBinding()
        binding.container.setOnClickListener {
            itemClick(it, holder.adapterPosition, binding)
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemClick(item: View, position: Int, binding: ChannelsListItemBinding) {
        val current = listVideo[position]
        currentId = current.id
        binding.current = currentId
        notifyDataSetChanged()
        listener?.onItemClick(item, current)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    interface OnItemClickListener {
        fun onItemClick(item: View, current: ItemVideo)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listVideo[position], currentId)
    }

    override fun getItemCount(): Int = listVideo.size
    override fun getItemId(position: Int): Long = listVideo[position].id

    fun getItemPosition(item: ItemVideo): Int = listVideo.indexOf(item)

    fun getItemFromPosition(position: Int): ItemVideo = listVideo[position]

    fun setCurrentId(current: Long ) {
        currentId = current
    }
    fun getCurrentId(): Long = currentId

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<ItemVideo>) {
        listVideo = list.take(10)
        notifyDataSetChanged()
    }

}