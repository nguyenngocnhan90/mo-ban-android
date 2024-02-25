package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.UserItemHandler
import com.moban.model.data.user.User
import com.moban.view.viewholder.RankItemViewHolder

/**
 * Created by LenVo on 6/21/18.
 */
class FeedRankAdapter : AbsAdapter<RankItemViewHolder>() {
    var users: MutableList<User> = ArrayList()
    var listener: UserItemHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_rank
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return RankItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.count()
    }

    override fun updateView(holder: RankItemViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user, position, false)

        holder.itemView.setOnClickListener {
            listener?.onClicked(user)
        }
    }
}
