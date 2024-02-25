package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.PostImageItemHandler
import com.moban.model.data.BitmapUpload
import com.moban.view.viewholder.PostImageItemViewHolder
import kotlinx.android.synthetic.main.item_post_image.view.*

/**
 * Created by LenVo on 4/17/18.
 */
class PostImageAdapter : AbsAdapter<PostImageItemViewHolder>() {

    var images: MutableList<BitmapUpload> = ArrayList()
    var listener: PostImageItemHandler? = null


    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_post_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PostImageItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.count()
    }

    override fun updateView(holder: PostImageItemViewHolder, position: Int) {
        if (position < images.size) {
            val image = images[position]
            holder.bind(image)

            holder.itemView.item_image_remove.setOnClickListener {
                listener?.onRemove(image)
            }
        }
    }

    fun removeImage(bitmap: BitmapUpload) {
        var idx = -1
        images.forEachIndexed { index, bitmapUpload ->
            if (bitmapUpload.time == bitmap.time) {
                idx = index
            }
        }
        if (idx != -1) {
            images.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }
}
