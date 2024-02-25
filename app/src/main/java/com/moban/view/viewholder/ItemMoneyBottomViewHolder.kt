package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.extension.toFullPriceString
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_money_bottom_sheet.view.*

/**
 * Created by LenVo on 12/23/18.
 */
class ItemMoneyBottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(value: Int, selected: Boolean) {
        val context = itemView.context!!
        val tintColor = Util.getColor(context, if (selected)
            R.color.color_menu_selected else R.color.color_black)
        itemView.item_bottom_tv_value.setTextColor(tintColor)
        val money = value.toFullPriceString() + " " + context.getString(R.string.vnd)
        itemView.item_bottom_tv_value.text = money
        itemView.item_bottom_img_check.visibility = if (selected) View.VISIBLE else View.GONE
    }
}
