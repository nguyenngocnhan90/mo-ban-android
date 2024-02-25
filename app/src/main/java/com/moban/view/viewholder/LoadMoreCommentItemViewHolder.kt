package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import kotlinx.android.synthetic.main.item_load_more_comment.view.*

/**
 * Created by neal on 3/20/18.
 */
class LoadMoreCommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(number: Int) {
        val context = itemView.context
        val text = context.getString(R.string.feed_load_more_comment, number)
        itemView.item_load_more_comment_tv_content.text = text
    }

}
