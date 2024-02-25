package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_dialog.view.*

/**
 * Created by LenVo on 7/26/18.
 */
class HostItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(host: String) {
        itemView.item_dialog_text.text = host
    }

}
