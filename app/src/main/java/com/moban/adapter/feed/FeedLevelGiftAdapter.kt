package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.LevelGiftHandler
import com.moban.model.data.user.LevelGift
import com.moban.view.viewholder.FeedLevelGiftItemViewHolder

class FeedLevelGiftAdapter : AbsAdapter<FeedLevelGiftItemViewHolder>() {
    var gifts: MutableList<LevelGift> = ArrayList()
    var listener: LevelGiftHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_new_deal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedLevelGiftItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return FeedLevelGiftItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gifts.size
    }

    override fun updateView(holder: FeedLevelGiftItemViewHolder, position: Int) {
        val item = gifts[position]
        holder.bind(item)
    }
}
