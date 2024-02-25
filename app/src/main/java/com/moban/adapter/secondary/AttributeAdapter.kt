package com.moban.adapter.secondary

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.secondary.constant.Attribute
import com.moban.view.viewholder.TextItemViewHolder

class AttributeAdapter : AbsAdapter<TextItemViewHolder>() {
    private var data: MutableList<Attribute> = ArrayList()
    var listener: AttributeItemHandler? = null

    interface AttributeItemHandler {
        fun onSelect(item: Attribute)
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
        holder.bind(item.attribute_Name)

        holder.itemView.setOnClickListener {
            listener?.onSelect(item)
        }
    }

    fun setDataList(dataList: List<Attribute>) {
        this.data.clear()
        this.data.addAll(dataList)
        notifyDataSetChanged()
    }
}
