package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.homedeal.HomeDeal
import com.moban.view.custom.CountDownView
import com.moban.view.viewholder.HomeDealItemViewHolder

/**
 * Created by lenvo on 5/25/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class FeedHomeDealAdapter : AbsAdapter<HomeDealItemViewHolder>() {

    private var homeDeals: MutableList<HomeDeal> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_feed_new_deal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeDealItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return HomeDealItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return homeDeals.count()
    }

    override fun updateView(holder: HomeDealItemViewHolder, position: Int) {
        val document = homeDeals[position]
        holder.bind(document, object: CountDownView.Handler {
            override fun onTimeOut() {
                homeDeals.removeAt(position)
                notifyDataSetChanged()
            }
        })
    }

    fun setHomeDealsList(homeDealList: List<HomeDeal>) {
        this.homeDeals.clear()
        this.homeDeals.addAll(homeDealList)
        notifyDataSetChanged()
    }

}
