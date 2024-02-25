package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_dialog.view.*

/**
 * Created by LenVo on 5/26/19.
 */
class TextItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(value: String) {
        val context = itemView.context
        itemView.item_dialog_text.text = value
    }

}
