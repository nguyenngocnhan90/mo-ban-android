package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.adapter.project.ProjectTypeAdapter
import com.moban.handler.UserItemHandler
import com.moban.model.data.statistic.Statistic
import kotlinx.android.synthetic.main.item_feed_statistics.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedStatistictemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener : UserItemHandler? = null
    val projectTypeAdapter: ProjectTypeAdapter = ProjectTypeAdapter(ProjectTypeAdapter.PROJECT_TYPE_HOME)

    fun bind(statistics: List<Statistic>) {
        val context = itemView.context

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.item_feed_statistics_recycle_view.layoutManager = layoutManager
        itemView.item_feed_statistics_recycle_view.adapter = projectTypeAdapter
        projectTypeAdapter.setStatisticsList(statistics)
    }
}
