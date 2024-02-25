package com.moban.view.viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.google.gson.Gson
import com.moban.adapter.feed.FeedUserAdapter
import com.moban.handler.UserItemHandler
import com.moban.model.data.post.Post
import com.moban.model.data.post.PostUsers
import com.moban.model.data.user.User
import kotlinx.android.synthetic.main.item_feed_new_partner.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedNewPartnerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener : UserItemHandler? = null
    val feedUserAdapter: FeedUserAdapter = FeedUserAdapter()

    fun bind(post: Post) {
        val context = itemView.context
        val postUsers = Gson().fromJson(post.post_Content, PostUsers::class.java)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.feed_new_partner_recycleView.layoutManager = layoutManager
        itemView.feed_new_partner_recycleView.adapter = feedUserAdapter

        feedUserAdapter.users = postUsers.users.toMutableList()
        feedUserAdapter.notifyDataSetChanged()
        feedUserAdapter.listener = object : UserItemHandler {
            override fun onClicked(user: User) {
                listener?.onClicked(user)
            }
        }
    }
}
