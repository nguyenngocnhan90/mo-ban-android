package com.moban.view.viewholder

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.statistic.Statistic
import com.moban.util.Font
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_project_type.view.*
import kotlinx.android.synthetic.main.item_statistic.view.*

/**
 * Created by LenVo on 7/17/18.
 */
class ProjectTypeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(statistic: Statistic, width: Int, selectedStatistic: Statistic) {
        val context = itemView.context
        val layoutParam = LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        itemView.layoutParams = layoutParam
        itemView.item_project_type_tv_num.text = statistic.count.toString()
        itemView.item_project_type_tv_name.text = statistic.type

        val selected = statistic.id == selectedStatistic.id
        arrayOf(itemView.item_project_type_tv_name,
                itemView.item_project_type_tv_num)
                .forEach {
                    it.typeface = Font.boldTypeface(context)
                    it.setTextColor(Util.getColor(context, if(selected) R.color.white else R.color.color_white_50))
                }
    }

    fun bind(statistic: Statistic) {
        val context = itemView.context
        itemView.item_statistic_tv_name.text = statistic.type
        itemView.item_statistic_tv_desc.text = statistic.count.toString()
    }
}
