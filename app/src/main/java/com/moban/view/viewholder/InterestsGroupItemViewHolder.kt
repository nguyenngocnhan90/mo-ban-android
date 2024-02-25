package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.adapter.interests.InterestAdapter
import com.moban.handler.InterestItemHandler
import com.moban.model.data.user.Interest
import com.moban.model.data.user.InterestGroup
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.item_interests_group.view.*

class InterestsGroupItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val interestAdapter: InterestAdapter = InterestAdapter()

    fun bind(interestsGroup: InterestGroup, selectedInterest: HashMap<Int, Interest>, listener: InterestItemHandler) {
        val context = itemView.context
        val groupName = interestsGroup.group + if (interestsGroup.required) "*" else ""
        itemView.item_interests_group_tv_name.text = groupName
        val layoutManager = CustomLinearLayoutManager(context)
        itemView.item_interests_group_recycleView.layoutManager = layoutManager
        itemView.item_interests_group_recycleView.adapter = interestAdapter
        interestAdapter.listener = listener
        interestAdapter.selectedSet = selectedInterest
        interestAdapter.setInterestList(interestsGroup.items.toMutableList())
    }

}
