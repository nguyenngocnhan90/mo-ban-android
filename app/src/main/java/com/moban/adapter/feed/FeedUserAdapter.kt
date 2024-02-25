package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.UserItemHandler
import com.moban.model.data.user.User
import com.moban.view.viewholder.UserItemViewHolder

/**
 * Created by LenVo on 6/18/18.
 */
class FeedUserAdapter(private val type: Int = USER_BIRTHDAY) : AbsAdapter<UserItemViewHolder>() {
    companion object {
        const val USER_BIRTHDAY: Int = 0
        const val USER_TOP_PARTNER: Int = 1
    }

    var users: MutableList<User> = ArrayList()
    var listener: UserItemHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        if (type == USER_TOP_PARTNER) {
            return R.layout.item_partner_circle
        }
        return R.layout.item_birthday
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return UserItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.count()
    }

    override fun updateView(holder: UserItemViewHolder, position: Int) {
        val user = users[position]
        if (type == USER_TOP_PARTNER) {
            holder.bindTopPartner(user)
        } else {
            holder.bind(user)
        }

        holder.itemView.setOnClickListener {
            listener?.onClicked(user)
        }
    }
}
