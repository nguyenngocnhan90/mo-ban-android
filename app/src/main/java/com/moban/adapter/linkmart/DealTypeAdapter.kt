package com.moban.adapter.linkmart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.DealTypeItemHandler
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.util.Device
import com.moban.view.viewholder.DealTypeItemViewHolder

/**
 * Created by LenVo on 8/15/18.
 */
class DealTypeAdapter : AbsAdapter<DealTypeItemViewHolder>() {

    var listener: DealTypeItemHandler? = null
    var categories: MutableList<LinkmartCategory> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_gift_type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealTypeItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return DealTypeItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.count()
    }

    override fun updateView(holder: DealTypeItemViewHolder, position: Int) {
        val minWidth = Device.getScreenWidth()/4
        if (position < categories.size) {
            val category = categories[position]
            holder.bind(category, minWidth)
        }
    }

    fun setCategoriesList(categories: List<LinkmartCategory>) {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }
}
