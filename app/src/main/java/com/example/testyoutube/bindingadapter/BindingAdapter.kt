package com.example.testyoutube.bindingadapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.utils.Constants.COUNT_HORIZONTAL_ITEMS

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(imageView: ImageView, item: ItemVideo) {
        Glide.with(imageView.context).load(item.imageUrl)
            .override(item.imageWidth, item.imageHeight)
            .placeholder(R.drawable.image_music)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("imageMini","imageAudio")
    fun ImageView.loadImageMini(imageMini: String?, imageAudio: Boolean) {
        if(imageMini!=null) {
            Glide.with(this.context).load(imageMini)
                .placeholder(if(imageAudio) R.drawable.audio else R.drawable.square)
                .centerCrop()
                .into(this)
        }
    }

    @JvmStatic
    @BindingAdapter("loadImageAudio")
    fun loadImageAudio(imageView: ImageView, image: String) {
        Glide.with(imageView.context).load(image)
            .placeholder(R.drawable.audio)
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
        view.visibility = if( isVisible ) VISIBLE else INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("firstText","firstSize","firstLoad")
    fun TextView.setFirstTitle(firstText: String?, firstSize: Int, firstLoad: Boolean) {
        this.text =
            if (firstLoad) {
                this.context.getString(R.string.loading)
            } else {
                if (firstSize < COUNT_HORIZONTAL_ITEMS) {
                    "${this.context.getString(R.string.search_result)} ($firstSize) $firstText"
                } else {
                    "${this.context.getString(R.string.full_result)}$COUNT_HORIZONTAL_ITEMS. $firstText"
                }
            }
    }

    @JvmStatic
    @BindingAdapter("secondText","secondSize","secondLoad")
    fun TextView.setSecondTitle(secondText: String?, secondSize: Int, secondLoad: Boolean) {
        this.text =
            if(secondLoad) {
                this.context.getString(R.string.loading)
            } else {
                if (secondSize < COUNT_HORIZONTAL_ITEMS) {
                    "${this.context.getString(R.string.search_result)} ($secondSize) $secondText"
                } else {
                    "${this.context.getString(R.string.full_result)}$secondSize. $secondText"
                }
            }
    }


}

