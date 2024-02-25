package com.moban.adapter.secondary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.SecondaryProjectItemHandler
import com.moban.model.data.secondary.SecondaryProject
import com.moban.view.viewholder.SecondaryProjectItemViewHolder

class SecondaryProjectAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_LINKMART = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: SecondaryProjectItemHandler? = null
    var projects: MutableList<SecondaryProject> = ArrayList()

    var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_secondary_project
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecondaryProjectItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return SecondaryProjectItemViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position < projects.count()) {
            return TYPE_LINKMART
        }

        return TYPE_LOAD_MORE
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

            TYPE_LINKMART -> {
                val viewHolder = holder as SecondaryProjectItemViewHolder

                if (position < projects.count()) {
                    val linkmart = projects[position]
                    viewHolder.bind(linkmart, position)

                    val itemView = holder.itemView
                    itemView.setOnClickListener {
                        listener?.onClicked(linkmart)
                    }
                }
            }
        }
    }

    fun setListSecondaryProject(secondaryList: List<SecondaryProject>, isLoadMoreAvailable: Boolean = false) {
        projects.clear()
        appendSecondaryProject(secondaryList, isLoadMoreAvailable)
    }

    fun appendSecondaryProject(linkmartList: List<SecondaryProject>, isLoadMoreAvailable: Boolean) {
        projects.addAll(linkmartList)
        this.isLoadMoreAvailable = isLoadMoreAvailable
        notifyDataSetChanged()
        isLoading = false
    }

    fun clearSecondaryProjects() {
        projects.clear()
        this.isLoadMoreAvailable = false
        notifyDataSetChanged()
        isLoading = false
    }
}
