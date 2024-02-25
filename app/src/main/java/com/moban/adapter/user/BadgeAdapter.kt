package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.BadgeItemHandler
import com.moban.model.data.user.Badge
import com.moban.view.viewholder.BadgeItemViewHolder

class BadgeAdapter : AbsAdapter<BadgeItemViewHolder>() {

    var badges: List<Badge> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: BadgeItemHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return BadgeItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return badges.count()
    }

    override fun onBindViewHolder(holder: BadgeItemViewHolder, position: Int) {
        if (position < badges.count()) {
            val badge = badges[position]

            holder.bind(badge)
            holder.itemView.setOnClickListener {
                listener?.onClicked(badge)
            }
        }
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_badge
    }

    override fun updateView(holder: BadgeItemViewHolder, position: Int) {

    }
}
