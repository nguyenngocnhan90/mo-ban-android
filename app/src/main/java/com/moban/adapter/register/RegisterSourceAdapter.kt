package com.moban.adapter.register

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.InterestItemHandler
import com.moban.view.viewholder.InterestItemViewHolder
import kotlinx.android.synthetic.main.item_interest.view.*

class RegisterSourceAdapter : AbsAdapter<InterestItemViewHolder>() {
    var sources: List<String> = ArrayList()
    var selectedSource: String = ""
    var listener: InterestItemHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_interest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return InterestItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sources.count()
    }

    override fun updateView(holder: InterestItemViewHolder, position: Int) {
        val item = sources[position]
        holder.bindSource(item)
        holder.itemView.item_interest_cb.setImageResource(if (item == selectedSource)
            R.drawable.check else R.drawable.uncheck)

        holder.itemView.setOnClickListener {
            selectedSource = item
            notifyDataSetChanged()
        }
    }
}