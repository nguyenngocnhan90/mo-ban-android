package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.deal.Deal
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_deal_today.view.*

/**
 * Created by LenVo on 6/21/18.
 */
class DealTodayItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(deal: Deal, position: Int) {
        val context = itemView.context
        itemView.setBackgroundColor(if (position%2 == 0) Util.getColor(context, R.color.white)
                                    else Util.getColor(context, R.color.color_gray_background_50))
        itemView.item_deal_today_tv_flat_id.text = deal.flat_ID
        itemView.item_deal_today_tv_project.text = deal.product_Name
        itemView.item_deal_today_tv_status.text = if (deal.getDealStatus() == null) "" else deal.getDealStatus()!!.getText(context)
    }

}
