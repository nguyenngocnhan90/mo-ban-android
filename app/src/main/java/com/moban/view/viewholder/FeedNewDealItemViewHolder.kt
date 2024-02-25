package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.adapter.feed.FeedLevelGiftAdapter
import com.moban.handler.LevelGiftHandler
import com.moban.model.data.user.LevelGift
import kotlinx.android.synthetic.main.item_feed_new_deal.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedNewDealItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val feedDealAdapter : FeedLevelGiftAdapter = FeedLevelGiftAdapter()

    fun bind(gifts: List<LevelGift>) {
        val context = itemView.context
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.item_feed_new_deal_recycle_view.layoutManager = layoutManager
        itemView.item_feed_new_deal_recycle_view.adapter = feedDealAdapter

        feedDealAdapter.gifts = gifts.toMutableList()
        feedDealAdapter.notifyDataSetChanged()
        feedDealAdapter.listener = object: LevelGiftHandler {
            override fun onClicked(gift: LevelGift) {

            }
        }
    }
}
