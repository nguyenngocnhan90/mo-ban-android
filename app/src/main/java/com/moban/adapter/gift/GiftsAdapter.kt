package com.moban.adapter.gift

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.GiftItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.gift.Gift
import com.moban.view.viewholder.GiftItemViewHolder
import com.moban.view.viewholder.LoadMoreViewHolder

/**
 * Created by LenVo on 4/11/18.
 */
class GiftsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_GIFT = 0
        private const val TYPE_LOAD_MORE = 1
    }

    var listener: GiftItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    var gifts: MutableList<Gift> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return when (viewType) {
            TYPE_GIFT -> GiftItemViewHolder(view)
            else -> LoadMoreViewHolder(view)
        }
    }

    private fun getLayoutResourceId(viewType: Int): Int {
        return if (viewType == TYPE_LOAD_MORE) {
            R.layout.item_load_more
        } else R.layout.item_gift
    }

    override fun getItemViewType(position: Int): Int {
        if (position < gifts.count()) {
            return TYPE_GIFT
        }

        return TYPE_LOAD_MORE
    }

    override fun getItemCount(): Int {
        return gifts.count() + if (isLoadMoreAvailable) 1 else 0
//        return deals.count()
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
                if (position < gifts.count()) {
                    val deal = gifts[position]

                    val viewHolder = holder as GiftItemViewHolder
                    viewHolder.bind(deal)

                    viewHolder.itemView.setOnClickListener {
                        listener?.onClicked(gifts[position])
                    }
                }
            }
        }
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setGifts(deals: List<Gift>, isLoadMoreAvailable: Boolean) {
        this.gifts = deals.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendGifts(deals: List<Gift>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.gifts.size + 1
        val length = deals.size

        this.gifts.addAll(deals)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }
}
