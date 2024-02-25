package com.moban.view.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.moban.flow.linkmart.list.LinkmartListActivity
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_gift_type.view.*

/**
 * Created by LenVo on 8/15/18.
 */
class DealTypeItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    private var context: Context? = null

    fun bind(category: LinkmartCategory, width: Int) {
        this.context = itemView.context
        itemView.item_deal_type_tv_category.text = category.name
        category.image?.let {
            LHPicasso.loadImage(it.url, itemView.item_deal_type_img_category)
        }

        arrayOf(itemView.item_deal_type_img_category, itemView.item_deal_type_tv_category).forEach { it ->
            it.setOnClickListener {
                LinkmartListActivity.start(context!!, category)
            }
        }

        val layoutParam = LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        itemView.layoutParams = layoutParam
    }
}
