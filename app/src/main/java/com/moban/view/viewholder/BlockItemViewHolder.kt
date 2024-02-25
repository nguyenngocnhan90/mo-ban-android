package com.moban.view.viewholder

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.project.Block
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_block.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class BlockItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    private var context: Context? = null

    fun bind(block: Block?, selected: Boolean) {
        this.context = itemView.context
        val name = block?.block_Name ?: "-"

        val isLongName = name.length >= 2
        itemView.item_block_tv_name_long.visibility = if (isLongName) View.VISIBLE else View.GONE
        itemView.item_block_tv_name_short.visibility = if (isLongName) View.GONE else View.VISIBLE
        val viewTvName = if (isLongName) itemView.item_block_tv_name_long else
            itemView.item_block_tv_name_short

        viewTvName.text = name
        val textColor = if (selected) Util.getColor(context!!, R.color.white) else
            Util.getColor(context!!, R.color.color_black)
        viewTvName.setTextColor(textColor)

        if (selected) {
            viewTvName.background = context!!.getDrawable(if (isLongName)
                R.drawable.background_button_round_20 else R.drawable.circle_icon_green)
        } else {
            viewTvName.background = ColorDrawable(Color.TRANSPARENT)
        }
    }
}
