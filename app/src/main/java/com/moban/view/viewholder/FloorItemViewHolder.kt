package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.project.Floor
import kotlinx.android.synthetic.main.item_dialog.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class FloorItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(floor: Floor) {
        val context = itemView.context
        itemView.item_dialog_text.text = floor.group
    }

}
