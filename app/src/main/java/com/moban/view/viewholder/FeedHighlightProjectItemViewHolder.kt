package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.LHApplication
import com.moban.adapter.project.ProjectAdapter
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.handler.ProjectItemHandler
import com.moban.model.data.project.Project
import kotlinx.android.synthetic.main.item_feed_highlight_project.view.*

/**
 * Created by LenVo on 21/6/18.
 */

class FeedHighlightProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val projectAdapter : ProjectAdapter = ProjectAdapter(ProjectAdapter.PROJECT_HORIZONTAL)

    fun bind() {
        val context = itemView.context
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.item_highlight_project_recycleview.layoutManager = layoutManager
        itemView.item_highlight_project_recycleview.adapter = projectAdapter
        projectAdapter.setProjects(LHApplication.instance.lhCache.highlightProject, false)

        projectAdapter.listener = object: ProjectItemHandler {
            override fun onClicked(project: Project) {
                ProjectDetailActivity.start(context, project)
            }

            override fun onFavorite(project: Project) {
            }
        }
    }
}
