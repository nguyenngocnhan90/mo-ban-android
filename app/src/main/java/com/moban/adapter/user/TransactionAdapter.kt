package com.moban.adapter.user

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.CoinTransactionItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.user.LhcTransaction
import com.moban.view.viewholder.LoadMoreViewHolder
import com.moban.view.viewholder.TransactionItemViewHolder

/**
 * Created by LenVo on 3/29/18.
 */
class TransactionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_TRANSACTION = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var transactions: MutableList<LhcTransaction> = ArrayList()
    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var listener: CoinTransactionItemHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return when (viewType) {
            TYPE_TRANSACTION -> TransactionItemViewHolder(view)
            else -> LoadMoreViewHolder(view)
        }
    }


    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_reward_transaction
    }

    override fun getItemViewType(position: Int): Int {
        if (position < transactions.count()) {
            return TYPE_TRANSACTION
        }

        return TYPE_LOAD_MORE
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

            else -> {
                if (position < transactions.count()) {
                    val transaction = transactions[position]

                    val viewHolder = holder as TransactionItemViewHolder
                    viewHolder.bind(transaction)

                    viewHolder.itemView.setOnClickListener {
                        listener?.onClicked(transaction)
                    }
                }
            }
        }
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setTransactions(deals: List<LhcTransaction>, isLoadMoreAvailable: Boolean) {
        this.transactions = deals.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendTransactions(deals: List<LhcTransaction>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.transactions.size + 1
        val length = deals.size

        this.transactions.addAll(deals)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }
}
