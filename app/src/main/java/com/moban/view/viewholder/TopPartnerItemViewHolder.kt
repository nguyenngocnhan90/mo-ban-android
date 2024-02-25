package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.adapter.feed.FeedUserAdapter
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.handler.UserItemHandler
import com.moban.model.data.user.User
import kotlinx.android.synthetic.main.item_feed_top_partner.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class TopPartnerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var listener : UserItemHandler? = null
    private val userAdapter: FeedUserAdapter = FeedUserAdapter(FeedUserAdapter.USER_TOP_PARTNER)

    fun bind(users: List<User>) {
        val context = itemView.context

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.item_feed_top_partner_recycle_view.layoutManager = layoutManager
        itemView.item_feed_top_partner_recycle_view.adapter = userAdapter

        userAdapter.users = users.toMutableList()
        userAdapter.notifyDataSetChanged()
        userAdapter.listener = object : UserItemHandler {
            override fun onClicked(user: User) {
                AccountDetailActivity.start(context, user.id)
            }
        }
    }
}
