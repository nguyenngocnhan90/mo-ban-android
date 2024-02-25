package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.model.data.BitmapUpload
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_post_image.view.*

/**
 * Created by LenVo on 4/17/18.
 */
class PostImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(bitmap: BitmapUpload) {
        itemView.item_image_view.adjustViewBounds = true

        if (bitmap.bitmap != null) {
            itemView.item_image_view.setImageBitmap(bitmap.bitmap)
        }
        else if (bitmap.photo != null) {
            bitmap.photo?.let {
                LHPicasso.loadImage(it.photo_Link, itemView.item_image_view)

                itemView.item_image_view.viewTreeObserver.addOnGlobalLayoutListener {
                    val width = itemView.item_image_view.width
                    val layout = itemView.item_image_view.layoutParams
                    if (it.width > 0) {
                        layout.height = it.height * width / it.width
                    }
                    itemView.item_image_view.layoutParams = layout
                }
            }
        }
        else if (!bitmap.url.isNullOrBlank()) {
            LHPicasso.loadImage(bitmap.url, itemView.item_image_view)
        }
    }
}
