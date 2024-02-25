package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.LevelGiftHandler
import com.moban.model.data.user.LevelGift
import com.moban.view.viewholder.LevelGiftItemViewHolder
import kotlinx.android.synthetic.main.item_level_gift.view.*

class LevelGiftAdapter : AbsAdapter<LevelGiftItemViewHolder>() {
    var gifts: MutableList<LevelGift> = ArrayList()
    var isShowStatus: Boolean = true
    var listener: LevelGiftHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_level_gift
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelGiftItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return LevelGiftItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gifts.size
    }

    override fun updateView(holder: LevelGiftItemViewHolder, position: Int) {
        val item = gifts[position]
        holder.bind(item, isShowStatus)
        holder.itemView.item_level_tv_status_gift.setOnClickListener {
            listener?.onClicked(item)
        }
    }

    fun setListGifts(giftsList: List<LevelGift>) {
        gifts.clear()
        gifts.addAll(giftsList)
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
