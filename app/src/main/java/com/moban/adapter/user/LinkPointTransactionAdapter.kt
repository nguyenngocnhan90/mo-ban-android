package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.handler.LinkPointTransactionItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.user.LinkPointTransaction
import com.moban.view.viewholder.CoinTransactionItemViewHolder
import com.moban.view.viewholder.LoadMoreViewHolder

class LinkPointTransactionAdapter:  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_COIN_TRANSACTION = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: LinkPointTransactionItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var transactions: MutableList<LinkPointTransaction> = ArrayList()

    val isDataEmpty: Boolean
        get() = transactions.isEmpty()

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_coin_transaction
    }

    override fun getItemViewType(position: Int): Int {
        if (position < transactions.count()) {
            return TYPE_COIN_TRANSACTION
        }
        return TYPE_LOAD_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)

        return when (viewType) {
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            else -> CoinTransactionItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return transactions.count() + if (isLoadMoreAvailable) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }

            TYPE_COIN_TRANSACTION -> {
                val viewHolder = holder as CoinTransactionItemViewHolder

                if (position < transactions.count()) {
                    val project = transactions[position]
                    viewHolder.bind(project)

                    val itemView = holder.itemView

                    itemView.setOnClickListener {
                        listener?.onClicked(project)
                    }
                }
            }
        }
    }
    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setTransactions(transactions: List<LinkPointTransaction>, isLoadMoreAvailable: Boolean) {
        this.transactions = transactions.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendTransactions(transactions: List<LinkPointTransaction>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.transactions.size
        val length = transactions.size

        this.transactions.addAll(transactions)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun reloadTransactions(transaction: LinkPointTransaction) {
        val index = transactions.indexOfFirst { p -> p.id == transaction.id }

        if (index >= 0 && index < transactions.count()) {
            transactions[index] = transaction
            notifyItemChanged(index)
        }
    }
}
