package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.google.gson.Gson
import com.moban.adapter.feed.FeedRankAdapter
import com.moban.flow.rank.TopPartnerActivity
import com.moban.handler.UserItemHandler
import com.moban.model.data.post.Post
import com.moban.model.data.post.PostRanks
import com.moban.model.data.user.User
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.item_feed_rank.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedRankItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val feedRankAdapter: FeedRankAdapter = FeedRankAdapter()
    var listener : UserItemHandler? = null

    fun bind(post: Post) {
        val context = itemView.context
        val postRanks = Gson().fromJson(post.post_Content, PostRanks::class.java)

        val layoutManager = CustomLinearLayoutManager(context)
        itemView.item_feed_rank_recycleView.layoutManager = layoutManager
        itemView.item_feed_rank_recycleView.adapter = feedRankAdapter

        feedRankAdapter.users = postRanks.other_Users.toMutableList()
        feedRankAdapter.notifyDataSetChanged()
        feedRankAdapter.listener = object : UserItemHandler {
            override fun onClicked(user: User) {
                listener?.onClicked(user)
            }
        }

        itemView.item_feed_rank_tv_view_detail.setOnClickListener {
            TopPartnerActivity.start(context)
        }
    }
}
