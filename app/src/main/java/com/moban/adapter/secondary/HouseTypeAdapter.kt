package com.moban.adapter.secondary

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.secondary.constant.HouseType
import com.moban.view.viewholder.TextItemViewHolder

class HouseTypeAdapter : AbsAdapter<TextItemViewHolder>() {
    private var data: MutableList<HouseType> = ArrayList()
    var listener: HouseTypeItemHandler? = null

    interface HouseTypeItemHandler {
        fun onSelect(item: HouseType)
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
        holder.bind(item.house_Type_Name)

        holder.itemView.setOnClickListener {
            listener?.onSelect(item)
        }
    }

    fun setDataList(dataList: List<HouseType>) {
        this.data.clear()
        this.data.addAll(dataList)
        notifyDataSetChanged()
    }
}
