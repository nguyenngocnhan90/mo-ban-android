package com.moban.adapter.secondary

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.secondary.constant.Attribute
import com.moban.view.viewholder.CheckBoxItemViewHolder

class CheckboxMicsAdapter : AbsAdapter<CheckBoxItemViewHolder>() {
    private var data: MutableList<Attribute> = ArrayList()
    private var selectedSet: HashMap<Int, Attribute> = HashMap()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_checkbox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBoxItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return CheckBoxItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun updateView(holder: CheckBoxItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item.attribute_Name, selectedSet.containsKey(item.id))

        holder.itemView.setOnClickListener {
            if (!selectedSet.containsKey(item.id)) {
                selectedSet[item.id] = item
            } else {
                selectedSet.remove(item.id)
            }
            notifyDataSetChanged()
        }
    }

    fun setDataList(dataList: List<Attribute>) {
        this.data.clear()
        this.data.addAll(dataList)
        notifyDataSetChanged()
    }

    fun setDataListSelected(dataList: List<Attribute>) {
        for(item in dataList) {
            selectedSet[item.id] = item
        }
    }

    fun getDataListSelected(): List<Attribute> {
        return selectedSet.values.toList()
    }
}
