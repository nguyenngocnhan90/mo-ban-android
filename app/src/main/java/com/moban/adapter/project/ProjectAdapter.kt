package com.moban.adapter.project

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectItemHandler
import com.moban.model.data.project.Project
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.ProjectItemViewHolder
import kotlinx.android.synthetic.main.item_project.view.*

/**
 * Created by LenVo on 3/5/18.
 */

class ProjectAdapter(private val type: Int = PROJECT_VERTICAL) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val HOT_PROJECT_HORIZONTAL: Int = 0
        const val PROJECT_HORIZONTAL: Int = 1
        const val PROJECT_VERTICAL: Int = 2
        const val PROJECT_DETAIL_HORIZONTAL: Int = 3

        private const val TYPE_PROJECT = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: ProjectItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var projects: MutableList<Project> = ArrayList()

    val isDataEmpty: Boolean
        get() = projects.isEmpty()

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_project
    }

    override fun getItemViewType(position: Int): Int {
        if (position < projects.count()) {
            return TYPE_PROJECT
        }

        return TYPE_LOAD_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)

        return when (viewType) {
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            else -> ProjectItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return projects.count() + if (isLoadMoreAvailable) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }

            TYPE_PROJECT -> {
                val viewHolder = holder as ProjectItemViewHolder

                if (position < projects.count()) {
                    val project = projects[position]
                    viewHolder.bind(project, position, type)

                    val itemView = holder.itemView

                    itemView.setOnClickListener {
                        listener?.onClicked(project)
                    }

//                    itemView.item_project_imgFavorite.setOnClickListener({view ->
//                        listener?.onFavorite(project)
//                    })
                }
            }
        }
    }
    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setProjects(projects: List<Project>, isLoadMoreAvailable: Boolean) {
        this.projects = projects.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendProjects(projects: List<Project>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.projects.size
        val length = projects.size

        this.projects.addAll(projects)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun reloadProject(project: Project) {
        val index = projects.indexOfFirst { p -> p.id == project.id }

        if (index >= 0 && index < projects.count()) {
            projects[index] = project
            notifyItemChanged(index)
        }
    }

    fun removeProject(project: Project) {
        val index = projects.indexOfFirst { p -> p.id == project.id }

        if (index >= 0 && index < projects.count()) {
            projects.removeAt(index)
            notifyDataChanged()
        }
    }
}
