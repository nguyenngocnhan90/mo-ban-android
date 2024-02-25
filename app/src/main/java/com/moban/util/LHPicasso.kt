package com.moban.util

import android.widget.ImageView
import com.moban.LHApplication
import com.moban.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Created by neal on 3/10/18.
 */
class LHPicasso {
    companion object {
        fun loadImage(url: String?, imageView: ImageView, scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_INSIDE) {
            if (!url.isNullOrBlank()) {
                val picasso = Picasso.with(LHApplication.instance.applicationContext).load(url).fit()
                val requestCreator = when (scaleType) {
                    ImageView.ScaleType.CENTER_INSIDE -> picasso.centerInside()
                    else -> picasso.centerCrop()
                }
                requestCreator.into(imageView)
            }
        }

        fun loadAvatar(url: String, imageView: ImageView, scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP) {
            if (url.isNotEmpty()) {
                val handler = object : Callback {
                    override fun onError() {
                        imageView.setImageResource(R.drawable.default_avatar)
                    }

                    override fun onSuccess() {
                    }
                }
                val picasso = Picasso.with(LHApplication.instance.applicationContext).load(url).fit()
                val requestCreator = when (scaleType) {
                    ImageView.ScaleType.CENTER_INSIDE -> picasso.centerInside()
                    else -> picasso.centerCrop()
                }
                requestCreator.into(imageView, handler)
            }
            else {
                imageView.setImageResource(R.drawable.default_avatar)
            }
        }
    }
}
