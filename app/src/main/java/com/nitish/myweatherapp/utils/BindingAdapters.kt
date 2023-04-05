package com.nitish.myweatherapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.io.IOException

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            try {
                PicassoCache.getPicassoInstance(view.context).load(url).into(view)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
    }
}