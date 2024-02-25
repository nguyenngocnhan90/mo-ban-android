package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import kotlinx.android.synthetic.main.item_checkbox.view.*

class CheckBoxItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(checkbox: String, isSelected: Boolean) {
        itemView.item_checkbox_img.setImageResource(if (isSelected) R.drawable.check else R.drawable.uncheck)
        itemView.item_checkbox_tv.text = checkbox
    }

}
