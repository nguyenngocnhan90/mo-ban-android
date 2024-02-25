package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.salary.PersonalRevenue
import com.moban.util.NumberUtil
import kotlinx.android.synthetic.main.item_revenue.view.*

class RevenueItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(revenue: PersonalRevenue) {
        val context = itemView.context

        itemView.item_revenue_name.text = revenue.product_Name
        itemView.item_revenue_value.text = NumberUtil.formatPrice(revenue.product_Revenue.toDouble(), context)
    }
}
