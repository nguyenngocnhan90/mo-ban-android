package com.moban.adapter.secondary

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LoadMoreHandler
import com.moban.handler.SecondaryHouseItemHandler
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.view.viewholder.SecondaryHouseItemViewHolder

class SecondaryHouseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_LINKMART = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: SecondaryHouseItemHandler? = null
    var houses: MutableList<SecondaryHouse> = ArrayList()

    var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_secondary_house
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecondaryHouseItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return SecondaryHouseItemViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position < houses.count()) {
            return TYPE_LINKMART
        }

        return TYPE_LOAD_MORE
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

            TYPE_LINKMART -> {
                val viewHolder = holder as SecondaryHouseItemViewHolder

                if (position < houses.count()) {
                    val linkmart = houses[position]
                    viewHolder.bind(linkmart, position)

                    val itemView = holder.itemView
                    itemView.setOnClickListener {
                        listener?.onClicked(linkmart)
                    }
                }
            }
        }
    }

    fun setListSecondaryProject(secondaryList: List<SecondaryHouse>, isLoadMoreAvailable: Boolean = false) {
        houses.clear()
        appendSecondaryProject(secondaryList, isLoadMoreAvailable)
    }

    fun appendSecondaryProject(linkmartList: List<SecondaryHouse>, isLoadMoreAvailable: Boolean) {
        houses.addAll(linkmartList)
        this.isLoadMoreAvailable = isLoadMoreAvailable
        notifyDataSetChanged()
        isLoading = false
    }

    fun clearSecondaryProjects() {
        houses.clear()
        this.isLoadMoreAvailable = false
        notifyDataSetChanged()
        isLoading = false
    }
}
