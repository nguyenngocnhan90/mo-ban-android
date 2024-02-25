package com.moban.adapter.linkmart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.LoadMoreHandler
import com.moban.handler.PurchasedLinkmartItemHandler
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.view.viewholder.PurchasedLinkmartItemViewHolder

/**
 * Created by H. Len Vo on 9/8/18.
 */
class PurchasedLinkmartAdapter : AbsAdapter<PurchasedLinkmartItemViewHolder>() {
    var linkmartOrders: MutableList<LinkmartOrder> = ArrayList()
    var listener: PurchasedLinkmartItemHandler? = null

    private var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_voucher
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedLinkmartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PurchasedLinkmartItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return linkmartOrders.size
    }

    override fun updateView(holder: PurchasedLinkmartItemViewHolder, position: Int) {
        val order = linkmartOrders[position]
        holder.bind(order)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(order)
        }
    }

    fun setListPurchased(orders: List<LinkmartOrder>) {
        linkmartOrders.clear()
        linkmartOrders.addAll(orders)
        notifyDataSetChanged()
    }

    fun addOrder(order: LinkmartOrder) {
        linkmartOrders.add(order)
        notifyDataSetChanged()
    }
}
