package com.moban.adapter.project

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectItemHandler
import com.moban.model.data.project.Project
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.SearchProjectItemViewHolder
import kotlinx.android.synthetic.main.item_search_project.view.*

/**
 * Created by LenVo on 5/4/18.
 */

class SearchProjectAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_PROJECT = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: ProjectItemHandler? = null

    var projects: MutableList<Project> = ArrayList()

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    val isDataEmpty: Boolean
        get() = projects.isEmpty()

    fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_search_project
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
            else -> SearchProjectItemViewHolder(view)
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
                val viewHolder = holder as SearchProjectItemViewHolder

                if (position < projects.count()) {
                    val project = projects[position]
                    viewHolder.bind(project)

                    val itemView = holder.itemView

                    itemView.setOnClickListener {
                        listener?.onClicked(project)
                    }
                }
            }
        }
    }

    fun appendProjects(projects: List<Project>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.projects.size
        val length = projects.size

        this.projects.addAll(projects)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setProjects(projects: List<Project>, mIsLoadMoreAvailable: Boolean) {
        this.projects = projects.toMutableList()
        this.isLoadMoreAvailable = mIsLoadMoreAvailable

        notifyDataChanged()
    }
}
