package com.moban.adapter.interests

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.InterestItemHandler
import com.moban.model.data.user.Interest
import com.moban.model.data.user.InterestGroup
import com.moban.view.viewholder.InterestsGroupItemViewHolder

class InterestsGroupAdapter : AbsAdapter<InterestsGroupItemViewHolder>() {
    private var interests: MutableList<InterestGroup> = ArrayList()
    var selectedSet: HashMap<Int, Interest> = HashMap()
    var selectedSetGroup: HashMap<String, InterestGroup> = HashMap()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_interests_group
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestsGroupItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return InterestsGroupItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return interests.count()
    }

    override fun updateView(holder: InterestsGroupItemViewHolder, position: Int) {
        val item = interests[position]
        holder.bind(item, selectedSet, object: InterestItemHandler {
            override fun onSelected(interest: Interest) {
                selectedSet[interest.id] = interest
                selectedSetGroup[item.group] = item
            }

            override fun onUnSelected(interest: Interest) {
                selectedSet.remove(interest.id)
            }
        })
    }

    fun setInterestList(interestsList: List<InterestGroup>) {
        this.interests.clear()
        this.interests.addAll(interestsList)
        notifyDataSetChanged()
    }
}
