package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.KeywordProjectItemHandler
import com.moban.view.viewholder.KeywordItemViewHolder

/**
 * Created by LenVo on 3/17/18.
 */
class KeywordProjectAdapter : AbsAdapter<KeywordItemViewHolder>() {

    var listener: KeywordProjectItemHandler? = null

    var keywords: MutableList<String> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_suggestion_keyword
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return KeywordItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return keywords.count()
    }

    override fun updateView(holder: KeywordItemViewHolder, position: Int) {
        val keyword = keywords[position]
        holder.bind(keyword)

        holder.itemView.setOnClickListener {
            listener?.onClicked(keyword)
        }

    }

}
