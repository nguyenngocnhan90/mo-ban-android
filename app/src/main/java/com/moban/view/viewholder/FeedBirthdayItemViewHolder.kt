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
import kotlinx.android.synthetic.main.item_feed_birthday.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedBirthdayItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener : UserItemHandler? = null
    val feedBirthdayAdapter : FeedUserAdapter = FeedUserAdapter()

    fun bind(post: Post) {
        val context = itemView.context
        val postUsers = Gson().fromJson(post.post_Content, PostUsers::class.java)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.feed_birthday_recycleView.layoutManager = layoutManager
        itemView.feed_birthday_recycleView.adapter = feedBirthdayAdapter

        feedBirthdayAdapter.users = postUsers.users.toMutableList()
        feedBirthdayAdapter.notifyDataSetChanged()
        feedBirthdayAdapter.listener = object : UserItemHandler {
            override fun onClicked(user: User) {
                listener?.onClicked(user)
            }
        }
    }
}
