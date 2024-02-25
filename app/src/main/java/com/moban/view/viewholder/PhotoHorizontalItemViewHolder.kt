package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.extension.getLink
import com.moban.model.data.BitmapUpload
import com.moban.util.BitmapUtil
import com.moban.util.Device
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_horizontal_photo.view.*

class PhotoHorizontalItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(bitmap: BitmapUpload) {

        if (bitmap.bitmap != null) {
            itemView.item_horizontal_photo_img.setImageBitmap(bitmap.bitmap)
        } else if (!bitmap.url.isNullOrBlank()) {
            LHPicasso.loadImage(bitmap.url.getLink(), itemView.item_horizontal_photo_img)
        }

        itemView.item_horizontal_photo_tv_main.visibility = View.GONE
        itemView.item_horizontal_photo_add.visibility = View.GONE
        itemView.item_horizontal_photo_img.visibility = View.VISIBLE

        val param = itemView.layoutParams
        val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(itemView.context, 50)) / 3
        param.height = heightView
        param.width = heightView
        itemView.layoutParams = param
    }

    fun bindMainPhoto(bitmap: BitmapUpload) {
        bind(bitmap)
        itemView.item_horizontal_photo_tv_main.visibility = View.VISIBLE
    }

    fun bindCreatePhoto() {
        itemView.item_horizontal_photo_add.visibility = View.VISIBLE
        itemView.item_horizontal_photo_img.visibility = View.GONE
        itemView.item_horizontal_photo_tv_main.visibility = View.GONE
    }
}
