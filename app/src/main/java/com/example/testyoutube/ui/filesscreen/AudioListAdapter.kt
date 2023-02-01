package com.example.testyoutube.ui.filesscreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.R
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.databinding.AudioListItemBinding
import com.example.testyoutube.databinding.ContentListItemBinding

class AudioListAdapter : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var listAudio: List<ItemAudio> = emptyList()
    private var currentId: Long = 0

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = AudioListItemBinding.bind(itemView)

        fun bind(item: ItemAudio, currentId: Long) {
            binding.item = item
            binding.current = currentId
        }
        fun getBinding(): AudioListItemBinding = binding
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_list_item,parent,false)
        val holder = ViewHolder(view)
        val binding = holder.getBinding()
        binding.container.setOnClickListener {
            itemClick(holder.bindingAdapterPosition, binding)
        }
        return holder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemClick(position: Int, binding: AudioListItemBinding) {
        val current = listAudio[position]
        currentId = current.musicId
        binding.current = currentId
        notifyDataSetChanged()
        listener?.onItemClick(current)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    interface OnItemClickListener {
        fun onItemClick(current: ItemAudio)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listAudio[position], currentId)
    }

    override fun getItemCount(): Int = listAudio.size
    override fun getItemId(position: Int): Long = listAudio[position].musicId
    fun getItemPosition(item: ItemAudio?): Int = listAudio.indexOf(item)
    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentId(item: ItemAudio) {
        currentId = item.musicId
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<ItemAudio>) {
        listAudio = list
        currentId=list[0].musicId
        notifyDataSetChanged()
    }

}