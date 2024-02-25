package com.moban.adapter.feed

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.DealItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.chart.ChartData
import com.moban.model.data.deal.Deal
import com.moban.view.viewholder.DealTodayChartItemViewHolder
import com.moban.view.viewholder.DealTodayItemViewHolder
import com.moban.view.viewholder.DealTodayTitleItemViewHolder
import com.moban.view.viewholder.LoadMoreViewHolder

/**
 * Created by LenVo on 6/21/18.
 */
class FeedDealAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_DEAL = 0
        private const val TYPE_DEAL_CHART = 1
        private const val TYPE_DEAL_TITLE = 2
        private const val TYPE_LOAD_MORE = 3
    }

    var isIncludeChart = false
    var deals: MutableList<Deal> = ArrayList()
    var chartData: List<ChartData>? = null
    var listener: DealItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    private fun getLayoutResourceId(viewType: Int): Int {
        return when (viewType) {
            TYPE_LOAD_MORE -> R.layout.item_load_more
            TYPE_DEAL_CHART -> R.layout.item_today_deal_chart
            TYPE_DEAL_TITLE -> R.layout.item_today_deal_title
            else -> R.layout.item_deal_today
        }
    }

    private fun getHeaderCount(): Int {
        return if (isIncludeChart) 2 else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && isIncludeChart) {
            return TYPE_DEAL_CHART
        }

        if (position == 1 && isIncludeChart) {
            return TYPE_DEAL_TITLE
        }

        val realPos = position - getHeaderCount()
        if (realPos < deals.count()) {
            return TYPE_DEAL
        }

        return TYPE_LOAD_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return when (viewType) {
            TYPE_DEAL -> DealTodayItemViewHolder(view)
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            TYPE_DEAL_CHART -> DealTodayChartItemViewHolder(view)
            else -> DealTodayTitleItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return deals.size  + getHeaderCount() + if (isLoadMoreAvailable) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }

            TYPE_DEAL -> {
                val realPos = position - getHeaderCount()
                if (realPos < deals.count()) {
                    val deal = deals[realPos]

                    val viewHolder = holder as DealTodayItemViewHolder
                    viewHolder.bind(deal, realPos)

                    viewHolder.itemView.setOnClickListener {
                        listener?.onClicked(deals[realPos])
                    }
                }
            }

            TYPE_DEAL_CHART -> {
                val viewHolder = holder as DealTodayChartItemViewHolder
                viewHolder.bind(chartData)
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

        val from = this.deals.size + 1 + getHeaderCount()
        val length = deals.size

        this.deals.addAll(deals)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun setDataChart(data: List<ChartData>?) {
        chartData = data
        this.notifyItemChanged(0)
    }
}
