package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.salary.PersonalRevenue
import com.moban.view.viewholder.RevenueItemViewHolder

class PersonalRevenueAdapter : AbsAdapter<RevenueItemViewHolder>() {

    var personalRevenues: List<PersonalRevenue> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevenueItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return RevenueItemViewHolder(view)
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_revenue
    }

    override fun getItemCount(): Int {
        return personalRevenues.count()
    }

    override fun updateView(holder: RevenueItemViewHolder, position: Int) {
        if (position < itemCount) {
            holder.bind(personalRevenues[position])
        }
    }

}
