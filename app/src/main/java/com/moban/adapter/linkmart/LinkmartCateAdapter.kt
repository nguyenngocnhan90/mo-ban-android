package com.moban.adapter.linkmart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.LinkmartCateHandler
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.view.viewholder.BottomMenuItemViewHolder

/**
 * Created by H. Len Vo on 9/29/18.
 */
class LinkmartCateAdapter(private val currentCate: LinkmartCategory) : AbsAdapter<BottomMenuItemViewHolder>() {
    var listener: LinkmartCateHandler? = null
    var categories: MutableList<LinkmartCategory> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_menu_bottom_sheet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomMenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return BottomMenuItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun updateView(holder: BottomMenuItemViewHolder, position: Int) {
        val cate = categories[position]
        val selected = cate.id == currentCate.id
        holder.bind(cate, selected)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(cate)
        }
    }

    fun setListCategories(listCategories: List<LinkmartCategory>) {
        categories.clear()
        categories.addAll(listCategories)
        notifyDataSetChanged()
    }
}
