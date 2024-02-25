package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.adapter.feed.FeedHomeDealAdapter
import com.moban.model.data.homedeal.HomeDeal
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.item_feed_new_deals.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedHomeDealItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val homeDealAdapter : FeedHomeDealAdapter = FeedHomeDealAdapter()

    fun bind(homeDeals: List<HomeDeal>) {
        val context = itemView.context
        val layoutManager = CustomLinearLayoutManager(context)
        itemView.item_feed_new_deals_recycle_view.layoutManager = layoutManager
        itemView.item_feed_new_deals_recycle_view.adapter = homeDealAdapter
        homeDealAdapter.setHomeDealsList(homeDeals)
    }
}
