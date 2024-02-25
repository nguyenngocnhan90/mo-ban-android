package com.moban.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.handler.PromotionItemHandler
import com.moban.model.data.deal.Promotion
import com.moban.view.viewholder.PurchasedLinkmartItemViewHolder
import kotlinx.android.synthetic.main.item_interest.view.*

class PromotionAdapter : AbsAdapter<PurchasedLinkmartItemViewHolder>() {
    private var promotions: MutableList<Promotion> = ArrayList()
    var listener: PromotionItemHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_voucher
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedLinkmartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PurchasedLinkmartItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return promotions.count()
    }

    override fun updateView(holder: PurchasedLinkmartItemViewHolder, position: Int) {
        val item = promotions[position]
        holder.bindPromotion(item)

        holder.itemView.setOnClickListener {
            listener?.onSelected(item)
        }
    }

    fun setPromotionList(list: List<Promotion>) {
        this.promotions.clear()
        this.promotions.addAll(list)
        notifyDataSetChanged()
    }
}
