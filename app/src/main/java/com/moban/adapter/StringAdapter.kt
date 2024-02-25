package com.moban.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.StringMenuItemHandler
import com.moban.view.viewholder.TextItemViewHolder

class StringAdapter : AbsAdapter<TextItemViewHolder>() {
    private var data: MutableList<String> = ArrayList()
    var listener: StringMenuItemHandler? = null

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
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener?.onClicked(item)
        }
    }

    fun setDataList(dataList: List<String>) {
        this.data.clear()
        this.data.addAll(dataList)
        notifyDataSetChanged()
    }
}
