package com.moban.adapter.badge

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.BadgeItemHandler
import com.moban.model.data.user.Badge
import com.moban.view.viewholder.RewardBadgeItemViewHolder

class RewardBadgeAdapter : AbsAdapter<RewardBadgeItemViewHolder>() {

    var badges: List<Badge> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: BadgeItemHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardBadgeItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return RewardBadgeItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return badges.count()
    }

    override fun onBindViewHolder(holder: RewardBadgeItemViewHolder, position: Int) {
        val badge = badges[position]

        if (position < badges.count()) {
            holder.bind(badges[position])
            holder.itemView.setOnClickListener {
                listener?.onClicked(badge)
            }
        }
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_reward_badge
    }

    override fun updateView(holder: RewardBadgeItemViewHolder, position: Int) {

    }
}
