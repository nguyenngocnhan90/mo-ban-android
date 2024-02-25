package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.handler.DealItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.deal.Deal
import com.moban.view.viewholder.DealProjectItemViewHolder
import com.moban.view.viewholder.LoadMoreViewHolder
import kotlinx.android.synthetic.main.item_deal_project.view.*

/**
 * Created by LenVo on 3/30/18.
 */
class DealsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DEAL = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var isSameProject: Boolean = true
    var isMyDeal: Boolean = true
    var listener: DealItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var deals: MutableList<Deal> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return when (viewType) {
            TYPE_DEAL -> DealProjectItemViewHolder(view)
            else -> LoadMoreViewHolder(view)
        }
    }

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_deal_project
    }

    override fun getItemViewType(position: Int): Int {
        if (position < deals.count()) {
            return TYPE_DEAL
        }

        return TYPE_LOAD_MORE
    }

    override fun getItemCount(): Int {
        return deals.count() + if (isLoadMoreAvailable) 1 else 0
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
                if (position < deals.count()) {
                    val deal = deals[position]
                    val viewHolder = holder as DealProjectItemViewHolder
                    viewHolder.bind(deal, isSameProject, listener)

                    viewHolder.itemView.item_deal_view_option.visibility = if (isMyDeal) View.VISIBLE else View.GONE

                    viewHolder.itemView.setOnClickListener {
                        listener?.onClicked(deal)
                    }

                    viewHolder.itemView.item_deal_btn_option.setOnClickListener {
                        listener?.onLongClicked(deal)
                    }

                    viewHolder.itemView.setOnLongClickListener {
                        listener?.onLongClicked(deal)
                        true
                    }

                    viewHolder.itemView.item_deal_btn_main_action.setOnClickListener {
                        listener?.onMainAction(deal)
                    }
                }
            }
        }
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setDeals(deals: List<Deal>, isLoadMoreAvailable: Boolean) {
        this.deals = deals.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendDeals(deals: List<Deal>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.deals.size + 1
        val length = deals.size

        this.deals.addAll(deals)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun updateDeal(deal: Deal) {
        val index = deals.indexOfFirst { p -> p.id == deal.id }
        if (index >= 0 && index < deals.count()) {
            deals[index] = deal
            notifyItemChanged(index)
        }
    }
}
