package com.moban.adapter.interests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.InterestItemHandler
import com.moban.model.data.user.Interest
import com.moban.view.viewholder.InterestItemViewHolder
import kotlinx.android.synthetic.main.item_interest.view.*

class InterestAdapter : AbsAdapter<InterestItemViewHolder>() {
    private var interests: MutableList<Interest> = ArrayList()
    var listener: InterestItemHandler? = null
    var selectedSet: HashMap<Int, Interest> = HashMap()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_interest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return InterestItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return interests.count()
    }

    override fun updateView(holder: InterestItemViewHolder, position: Int) {
        val item = interests[position]
        holder.bind(item)
        holder.itemView.item_interest_cb.setImageResource(if (selectedSet.containsKey(item.id))
            R.drawable.check else R.drawable.uncheck)

        if (position == interests.size -1) {
            holder.itemView.item_interest_separate_line.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            for (interest in interests) {
                if (item.id == interest.id) {
                    selectedSet[interest.id] = interest
                    listener?.onSelected(interest)
                } else {
                    selectedSet.remove(interest.id)
                    listener?.onUnSelected(interest)
                }
            }
            notifyDataSetChanged()
        }
    }

    fun setInterestList(interestsList: List<Interest>) {
        this.interests.clear()
        this.interests.addAll(interestsList)
        notifyDataSetChanged()
    }
}
