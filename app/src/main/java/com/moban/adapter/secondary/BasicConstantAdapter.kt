package com.moban.adapter.secondary

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.secondary.constant.BasicConstantType
import com.moban.view.viewholder.TextItemViewHolder

class BasicConstantAdapter : AbsAdapter<TextItemViewHolder>() {
    private var data: MutableList<BasicConstantType> = ArrayList()
    var listener: BasicConstantTypeItemHandler? = null

    interface BasicConstantTypeItemHandler {
        fun onSelect(item: BasicConstantType)
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return TextItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun updateView(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item.name)

        holder.itemView.setOnClickListener {
            listener?.onSelect(item)
        }
    }

    fun setDataList(dataList: List<BasicConstantType>) {
        this.data.clear()
        this.data.addAll(dataList)
        notifyDataSetChanged()
    }
}
