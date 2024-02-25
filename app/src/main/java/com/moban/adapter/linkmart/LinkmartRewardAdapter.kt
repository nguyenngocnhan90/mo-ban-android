package com.moban.adapter.linkmart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.LinkmartRewardItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.user.LevelGift
import com.moban.view.viewholder.LinkmartRewardItemViewHolder
import kotlinx.android.synthetic.main.item_reward.view.*

/**
 * Created by H. Len Vo on 9/8/18.
 */
class LinkmartRewardAdapter : AbsAdapter<LinkmartRewardItemViewHolder>() {
    var gifts: MutableList<LevelGift> = ArrayList()
    var listener: LinkmartRewardItemHandler? = null

    var loadMoreHandler: LoadMoreHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_reward
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkmartRewardItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return LinkmartRewardItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gifts.size
    }

    override fun updateView(holder: LinkmartRewardItemViewHolder, position: Int) {
        val gift = gifts[position]
        holder.bind(gift)

        holder.itemView.item_reward_tv_status.setOnClickListener {
            if (gift.using_status == 0) {
                listener?.onReceivedGift(gift)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.onClicked(gift)
        }
    }

    fun setGiftList(orders: List<LevelGift>) {
        gifts.clear()
        gifts.addAll(orders)
        notifyDataSetChanged()
    }

    fun updateGift(gift: LevelGift) {
        val index = gifts.indexOfFirst { p -> p.gift_level_Id == gift.gift_level_Id }
        if (index >= 0 && index < gifts.count()) {
            gifts[index] = gift
            notifyItemChanged(index)
        }
    }
}
