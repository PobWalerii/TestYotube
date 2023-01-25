@file:Suppress("DEPRECATION")

package com.example.testyoutube.bindingadapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.testyoutube.R
import com.example.testyoutube.data.videolistitem.ItemVideo

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(imageView: ImageView, item: ItemVideo) {
        Glide.with(imageView.context).load(item.imageurl)
            .override(item.imagewidth, item.imageheight)
            .placeholder(R.drawable.music)
            .into(imageView)
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    @BindingAdapter("setButtonColor")
    fun setButtonColor(view: Button, active: Boolean) {
        val color = ContextCompat.getColor(view.context,
            if(active) {
                R.color.active_button
            } else {
                R.color.text_title_color
            })
        view.setTextColor(color)
        view.setCompoundDrawableTintList(ColorStateList.valueOf(color))
    }

    @JvmStatic
    @BindingAdapter("setVisible")
    fun setVisible(view: View, isVisible: Boolean) {
        view.visibility = if( isVisible ) VISIBLE else GONE
    }

}

