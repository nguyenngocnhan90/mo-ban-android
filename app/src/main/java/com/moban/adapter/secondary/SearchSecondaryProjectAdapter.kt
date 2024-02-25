package com.moban.adapter.secondary

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.SecondaryHouseItemHandler
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.SearchSecondaryProjectItemViewHolder

/**
 * Created by LenVo on 5/4/18.
 */

class SearchSecondaryProjectAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_PROJECT = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: SecondaryHouseItemHandler? = null

    var houses: MutableList<SecondaryHouse> = ArrayList()

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    val isDataEmpty: Boolean
        get() = houses.isEmpty()

    fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_search_project
    }

    override fun getItemViewType(position: Int): Int {
        if (position < houses.count()) {
            return TYPE_PROJECT
        }

        return TYPE_LOAD_MORE
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)

        return when (viewType) {
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            else -> SearchSecondaryProjectItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return houses.count() + if (isLoadMoreAvailable) 1 else 0
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
                val viewHolder = holder as SearchSecondaryProjectItemViewHolder

                if (position < houses.count()) {
                    val project = houses[position]
                    viewHolder.bind(project)

                    val itemView = holder.itemView

                    itemView.setOnClickListener {
                        listener?.onClicked(project)
                    }
                }
            }
        }
    }

    fun appendProjects(houses: List<SecondaryHouse>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.houses.size
        val length = houses.size

        this.houses.addAll(houses)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setProjects(houses: List<SecondaryHouse>, mIsLoadMoreAvailable: Boolean) {
        this.houses = houses.toMutableList()
        this.isLoadMoreAvailable = mIsLoadMoreAvailable

        notifyDataChanged()
    }
}
