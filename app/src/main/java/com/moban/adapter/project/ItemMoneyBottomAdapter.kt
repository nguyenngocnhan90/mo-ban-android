package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.ItemMoneyBottomHandler
import com.moban.view.viewholder.ItemMoneyBottomViewHolder

/**
 * Created by H. Len Vo on 12/23/18.
 */
class ItemMoneyBottomAdapter(private val current: Int?) : AbsAdapter<ItemMoneyBottomViewHolder>() {
    var listener: ItemMoneyBottomHandler? = null
    var values: MutableList<Int> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_money_bottom_sheet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMoneyBottomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return ItemMoneyBottomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun updateView(holder: ItemMoneyBottomViewHolder, position: Int) {
        val value = values[position]
        val selected = value == current
        holder.bind(value, selected)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(value)
        }
    }

    fun setListValues(values: List<Int>) {
        this.values.clear()
        this.values.addAll(values)
        notifyDataSetChanged()
    }
}
