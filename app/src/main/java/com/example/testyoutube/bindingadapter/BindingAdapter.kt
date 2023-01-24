@file:Suppress("DEPRECATION")

package com.example.testyoutube.bindingadapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.Image
import android.os.Build
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.testyoutube.R

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(imageView: ImageView, image: String?) {
        Glide.with(imageView.context).load(image)
            .transform(CenterInside(), RoundedCorners(24))
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

}

