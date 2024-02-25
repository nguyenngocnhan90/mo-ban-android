package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.view.viewholder.TextHeaderViewHolder

class HeaderAdapter : AbsAdapter<TextHeaderViewHolder>() {

    var currentIndex: Int = 0
    var texts: List<String> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return TextHeaderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return texts.count()
    }

    override fun onBindViewHolder(holder: TextHeaderViewHolder, position: Int) {
        if (position < texts.count()) {
            holder.bind(texts[position], position == currentIndex)

            recyclerItemListener?.let {
                holder.itemView.setOnClickListener { view ->
                    it.onClick(view, position)
                }
            }

        }
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_text_header
    }

    override fun updateView(holder: TextHeaderViewHolder, position: Int) {
    }
}
