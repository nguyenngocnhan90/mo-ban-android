package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.deal.Promotion
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_voucher.view.*

class PurchasedLinkmartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(linkmartOrder: LinkmartOrder) {
        val context = itemView.context!!
        val linkmart = linkmartOrder.line_items?.firstOrNull() ?: return
        linkmart.images?.firstOrNull()?.let {
            LHPicasso.loadImage(it.url, itemView.item_voucher_img_logo)
        }
        itemView.item_voucher_tv_name.text = linkmart.name
        val price = linkmartOrder.total + " " + context.getString(R.string.lhc)
        itemView.item_voucher_tv_price.text = price
    }

    fun bindPromotion(promotion: Promotion) {
        val image = promotion.image
        if (image.isNotEmpty()) {
            LHPicasso.loadImage(image, itemView.item_voucher_img_logo)
        }

        itemView.item_voucher_tv_name.text = promotion.name
        itemView.item_voucher_tv_price.visibility = View.GONE
    }
}
