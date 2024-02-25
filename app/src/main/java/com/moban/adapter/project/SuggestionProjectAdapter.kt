package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.ProjectItemHandler
import com.moban.model.data.project.Project
import com.moban.view.viewholder.SuggestionProjectItemViewHolder
import kotlinx.android.synthetic.main.item_suggestion_project.view.*

/**
 * Created by LenVo on 5/4/18.
 */

class SuggestionProjectAdapter : AbsAdapter<SuggestionProjectItemViewHolder>() {

    var listener: ProjectItemHandler? = null

    var projects: MutableList<Project> = ArrayList()

    val isDataEmpty: Boolean
        get() = projects.isEmpty()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_suggestion_project
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionProjectItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return SuggestionProjectItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return projects.count()
    }

    override fun updateView(holder: SuggestionProjectItemViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)

        val itemView = holder.itemView

        itemView.item_suggestion_img_media.setOnClickListener {
            listener?.onClicked(project)
        }
    }

    fun appendProjects(projects: List<Project>) {
        this.projects.addAll(projects.toMutableList())
        this.notifyDataSetChanged()
    }
}
