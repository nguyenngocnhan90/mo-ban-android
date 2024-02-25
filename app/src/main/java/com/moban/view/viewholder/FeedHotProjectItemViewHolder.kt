package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.adapter.project.ProjectAdapter
import com.moban.enum.ProjectHighlightType
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.projects.highlight.HighlightProjectActivity
import com.moban.handler.ProjectItemHandler
import com.moban.model.data.project.Project
import kotlinx.android.synthetic.main.item_feed_hot_product.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedHotProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val projectAdapter: ProjectAdapter = ProjectAdapter(ProjectAdapter.HOT_PROJECT_HORIZONTAL)

    fun bind(projects: List<Project>, type: ProjectHighlightType) {
        val context = itemView.context

        if (type == ProjectHighlightType.RECENT) {
            itemView.feed_hot_product_tv.text = "Sản phẩm đã xem"
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.item_feed_hot_product_recycle_view.layoutManager = layoutManager
        itemView.item_feed_hot_product_recycle_view.adapter = projectAdapter
        itemView.feed_hot_product_btn_all.setOnClickListener {
            HighlightProjectActivity.start(context, type)
        }

        projectAdapter.projects = projects.toMutableList()
        projectAdapter.notifyDataSetChanged()
        projectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                ProjectDetailActivity.start(context, project)
            }

            override fun onFavorite(project: Project) {

            }
        }
    }
}
