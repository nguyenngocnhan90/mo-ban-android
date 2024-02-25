package com.moban.adapter.linkmart

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.LinkmartItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.linkmart.Linkmart
import com.moban.view.viewholder.LinkmartItemViewHolder

class LinkmartAdapter(private val type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val  LINKMART_HORIZONTAL: Int = 0
        const val LINKMART_VERTICAL: Int = 1

        private const val TYPE_LINKMART = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: LinkmartItemHandler? = null
    var linkmarts: MutableList<Linkmart> = ArrayList()

    var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_linkmart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkmartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return LinkmartItemViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position < linkmarts.count()) {
            return TYPE_LINKMART
        }

        return TYPE_LOAD_MORE
    }

    override fun getItemCount(): Int {
        return linkmarts.count() + if (isLoadMoreAvailable) 1 else 0
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
                val viewHolder = holder as LinkmartItemViewHolder

                if (position < linkmarts.count()) {
                    val linkmart = linkmarts[position]
                    viewHolder.bind(linkmart, type)

                    val itemView = holder.itemView
                    itemView.setOnClickListener {
                        listener?.onClicked(linkmart)
                    }
                }
            }
        }
    }

    fun setListLinkmarts(linkmartList: List<Linkmart>) {
        linkmarts.clear()
        appendLinkmarts(linkmartList)
    }

    fun setListLinkmartsAndRefresh(linkmartList: List<Linkmart>) {
        linkmarts.clear()
        appendLinkmarts(linkmartList)
        notifyDataSetChanged()
    }

    fun appendLinkmarts(linkmartList: List<Linkmart>) {
        linkmarts.addAll(linkmartList)
    }

    fun setIsLoadMore(isLoadMore: Boolean) {
        this.isLoadMoreAvailable = isLoadMore
        notifyDataSetChanged()
        isLoading = false
    }

    fun clearLinkmarts() {
        linkmarts.clear()
        this.isLoadMoreAvailable = false
        notifyDataSetChanged()
        isLoading = false
    }
}
