package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.Photo
import com.moban.model.data.project.Project
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(photo: Photo) {
        LHPicasso.loadImage(photo.photo_Link, itemView.item_photo_image)
    }

    fun bind(photo: Project) {
        LHPicasso.loadImage(photo.product_Icon, itemView.item_photo_image)
    }
}
