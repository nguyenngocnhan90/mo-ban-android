package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.google.gson.Gson
import com.moban.adapter.feed.FeedDealAdapter
import com.moban.flow.todaydeal.TodayDealActivity
import com.moban.handler.DealItemHandler
import com.moban.model.data.post.Post
import com.moban.model.data.post.PostDeals
import com.moban.model.data.deal.Deal
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.item_feed_deals.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedDealItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener : DealItemHandler? = null
    val feedDealAdapter : FeedDealAdapter = FeedDealAdapter()

    fun bind(post: Post, listener: DealItemHandler?) {
        val context = itemView.context
        this.listener = listener
        val postDeals = Gson().fromJson(post.post_Content, PostDeals::class.java)

        val layoutManager = CustomLinearLayoutManager(context)
        itemView.item_feed_deals_recycleView.layoutManager = layoutManager
        itemView.item_feed_deals_recycleView.adapter = feedDealAdapter

        feedDealAdapter.deals = postDeals.deals.toMutableList()
        feedDealAdapter.notifyDataSetChanged()
        feedDealAdapter.listener = object : DealItemHandler {
            override fun onClicked(deal: Deal) {
                listener?.onClicked(deal)
            }

            override fun onLongClicked(deal: Deal) {
            }

            override fun onMainAction(deal: Deal) {

            }

            override fun onTimeOut() {
            }
        }

        itemView.item_feed_deals_tv_view_detail.setOnClickListener {
            TodayDealActivity.start(context)
        }
    }
}
