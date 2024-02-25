package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.util.Font
import kotlinx.android.synthetic.main.item_text_header.view.*

class TextHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(text: String, isSelected: Boolean) {
        val context = itemView.context

        val font = if (isSelected) Font.boldTypeface(context) else Font.mediumTypeface(context)

        itemView.item_text_header_tv_content.typeface = font
        itemView.item_text_header_tv_content.alpha = if (isSelected) 1.0f else 0.7f

        itemView.item_text_header_tv_content.text = text
    }
}
