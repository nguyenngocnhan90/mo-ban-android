package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.user.Badge
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_badge.view.*

class BadgeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(badge: Badge) {
        LHPicasso.loadImage(badge.badge_Image, itemView.item_badge_image)
    }

}
