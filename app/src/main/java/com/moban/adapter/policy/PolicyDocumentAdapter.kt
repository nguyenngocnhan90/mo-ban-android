package com.moban.adapter.policy

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.extension.toInt
import com.moban.handler.DocumentItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.document.Document
import com.moban.util.Device
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.PolicyDocumentItemViewHolder

/**
 * Created by LenVo on 3/25/18.
 */

class PolicyDocumentAdapter(private val type: Int = HORIZONTAL) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val  HORIZONTAL: Int = 0
        const val VERTICAL: Int = 1

        private const val TYPE_DOCUMENT = 0
        private const val TYPE_LOAD_MORE = 1
    }
    var isProjectPolicy = false
    var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var listener: DocumentItemHandler? = null
    var isSpecialDeal: Boolean = false
    var isFlashSaleDeal: Boolean = false
    var documents: MutableList<Document> = ArrayList()

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_policy
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("DEBUGLEN", "getItemViewType position= $position")
        if (position < documents.count()) {
            return TYPE_DOCUMENT
        }

        return TYPE_LOAD_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return when (viewType) {
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            else -> PolicyDocumentItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        Log.i("DEBUGLEN", "getItemCount ${documents.count() + isLoadMoreAvailable.toInt}")
        return documents.count() + isLoadMoreAvailable.toInt
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i("DEBUGLEN", "updateView position= $position, holder.itemViewType = ${holder.itemViewType}")
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
                val param = holder.itemView.layoutParams
                val widthView = Device.getScreenWidth()
                param.width = widthView
                holder.itemView.layoutParams = param
            }

            TYPE_DOCUMENT -> {
                holder as PolicyDocumentItemViewHolder
                val document = documents[position]
                holder.bind(document, isSpecialDeal, isFlashSaleDeal, type, isProjectPolicy)
            }
        }
    }

    fun setDocumentList(documentsList: List<Document>, isLoadMoreAvailable: Boolean = false) {
        this.isLoadMoreAvailable = isLoadMoreAvailable
        this.documents.clear()
        this.documents.addAll(documentsList)
        notifyDataChanged()
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun appendProjects(documentsList: List<Document>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.documents.size
        val length = documentsList.size

        this.documents.addAll(documentsList)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }
}
