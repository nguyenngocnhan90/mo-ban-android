package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.TopicItemHandler
import com.moban.model.data.post.Topic
import com.moban.view.viewholder.BottomMenuItemViewHolder

class TopicAdapter(private val current: Topic?) : AbsAdapter<BottomMenuItemViewHolder>() {
    var listener: TopicItemHandler? = null
    var topics: MutableList<Topic> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_menu_bottom_sheet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomMenuItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(getLayoutResourceId(viewType), parent, false)
        return BottomMenuItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun updateView(holder: BottomMenuItemViewHolder, position: Int) {
        val topic = topics[position]
        val selected = topic.id == current?.id
        holder.bind(topic, selected)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(topic)
        }
    }

    fun setListTopics(list: List<Topic>) {
        topics.clear()
        topics.addAll(list)
        notifyDataSetChanged()
    }
}