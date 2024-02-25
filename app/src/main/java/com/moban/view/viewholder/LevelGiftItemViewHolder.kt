package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.user.LevelGift
import kotlinx.android.synthetic.main.item_level_gift.view.*

class LevelGiftItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(gift: LevelGift, isShowStatus: Boolean) {
        val context = itemView.context

        itemView.item_level_tv_gift_name.text = gift.gift_Name
        itemView.item_level_tv_status_gift.visibility = if (isShowStatus) View.VISIBLE else View.GONE
        val drawable = context.getDrawable(if (gift.using_status == 0)
            R.drawable.background_button_linkhouse_round_4 else R.drawable.background_button_linkhouse_disable_round_4)
        itemView.item_level_tv_status_gift.background = drawable
        itemView.item_level_tv_status_gift.text = if (gift.using_status == 0) context.getString(R.string.get) else context.getString(R.string.got)
    }
}
