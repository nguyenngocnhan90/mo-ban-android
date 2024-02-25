package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.user.Interest
import kotlinx.android.synthetic.main.item_interest.view.*

class InterestItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(interest: Interest) {
//        val context = itemView.context
        itemView.item_interest_tv_name.text = interest.interest_Name
    }

    fun bindSource(text: String) {
        itemView.item_interest_tv_name.text = text
        itemView.item_interest_separate_line.visibility = View.GONE
    }
}
