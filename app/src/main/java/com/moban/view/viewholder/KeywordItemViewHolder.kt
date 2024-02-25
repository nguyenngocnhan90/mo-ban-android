package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.util.Font
import kotlinx.android.synthetic.main.item_suggestion_keyword.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class KeywordItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(keyword: String) {
        val context = itemView.context
        val fontRegular = Font.regularTypeface(context)

        itemView.item_suggestion_tv_keyword.typeface = fontRegular
        itemView.item_suggestion_tv_keyword.text = keyword
    }

}
