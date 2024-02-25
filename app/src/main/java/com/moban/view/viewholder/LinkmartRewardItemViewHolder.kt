package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.user.LevelGift
import com.moban.util.LHPicasso
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_reward.view.*

class LinkmartRewardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(gift: LevelGift) {
        val context = itemView.context!!
        LHPicasso.loadImage(gift.gift_Image, itemView.item_reward_img_logo)
        itemView.item_reward_tv_name.text = gift.gift_Name
        val isUsed = gift.using_status != 0

        val status = context.getString(if (isUsed) R.string.received else R.string.get_it)
        val color = if (isUsed) R.color.color_black_50 else R.color.color_green
        itemView.item_reward_tv_status.text = status
        itemView.item_reward_tv_status.setTextColor(Util.getColor(context, color))
        itemView.item_reward_tv_status.isEnabled = !isUsed
    }
}
