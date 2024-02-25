package com.moban.view.viewholder

import android.content.res.ColorStateList
import android.view.View
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.post.Topic
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_menu_bottom_sheet.view.*

/**
 * Created by LenVo on 8/15/18.
 */
class BottomMenuItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(linkmartCategory: LinkmartCategory, selected: Boolean) {
        val context = itemView.context!!
        val tintColor = Util.getColor(
            context, if (selected)
                R.color.color_menu_selected else R.color.color_black
        )
        ImageViewCompat.setImageTintList(
            itemView.menu_linkmart_cate_img,
            ColorStateList.valueOf(tintColor)
        )
        itemView.menu_linkmart_cate_tv.setTextColor(tintColor)
        itemView.menu_linkmart_cate_tv.text = linkmartCategory.name
        itemView.menu_linkmart_cate_img_check.visibility = if (selected) View.VISIBLE else View.GONE
    }

    fun bind(topic: Topic, selected: Boolean) {
        val context = itemView.context!!
        val tintColor = Util.getColor(
            context, if (selected)
                R.color.color_menu_selected else R.color.color_black
        )
        ImageViewCompat.setImageTintList(
            itemView.menu_linkmart_cate_img,
            ColorStateList.valueOf(tintColor)
        )
        itemView.menu_linkmart_cate_tv.setTextColor(tintColor)
        itemView.menu_linkmart_cate_tv.text = topic.topic
        itemView.menu_linkmart_cate_img_check.visibility = if (selected) View.VISIBLE else View.GONE
    }
}
