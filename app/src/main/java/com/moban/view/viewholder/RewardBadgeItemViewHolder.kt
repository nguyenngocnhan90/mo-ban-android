package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.user.Badge
import com.moban.util.DateUtil
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_reward_badge.view.*

class RewardBadgeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val DF_SIMPLE_STRING = "dd/MM/yyyy"
    }

    fun bind(badge: Badge) {
        itemView.item_reward_badge_name.text = badge.badge_Name
        itemView.item_reward_badge_date.text = DateUtil.dateStringFromSeconds(badge.created_Date.toLong(),
                DF_SIMPLE_STRING)

        LHPicasso.loadImage(badge.badge_Image, itemView.item_reward_badge_image)
    }

}
