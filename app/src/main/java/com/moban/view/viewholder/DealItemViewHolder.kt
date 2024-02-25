package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.deal.Deal
import com.moban.util.Device
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_deal.view.*

/**
 * Created by LenVo on 3/30/18.
 */
class DealItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(deal: Deal) {
        val context = itemView.context

        val param = itemView.item_deal_imgMedia.layoutParams
        param.height = Device.getScreenWidth() * 9 / 16
        itemView.item_deal_imgMedia.layoutParams = param

        LHPicasso.loadImage(deal.product_Image, itemView.item_deal_imgMedia)

        itemView.item_deal_tv_name.text = deal.product_Name

        val dealDate = context.getString(R.string.deals_date) + " " + deal.getDealDate()
        itemView.item_deal_tv_date.text = dealDate

        if (deal.flat_ID == null ) {
            itemView.item_deal_flat.visibility = View.INVISIBLE
        } else {
            itemView.item_deal_flat.text = deal.flat_ID
            deal.getDealStatus()?.let {
                itemView.item_deal_flat.background = it.getBackgroundRound5(context)
            }

        }

        itemView.item_deal_review.visibility = if (deal.has_Review) View.GONE else View.VISIBLE
    }

}
