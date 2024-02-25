package com.moban.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.handler.FeedCommentItemHandler
import com.moban.model.data.post.Comment
import com.moban.util.Util
import com.moban.view.viewholder.CommentItemViewHolder
import kotlinx.android.synthetic.main.item_comment.view.*

class FeedSubCommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var comments: List<Comment> = ArrayList()
    var listener: FeedCommentItemHandler? = null

    private fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_comment
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType),
            parent,
            false)
        return CommentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CommentItemViewHolder
        if (position < comments.count()) {
            val comment = comments[position]
            viewHolder.bind(comment)

            val itemView = holder.itemView

            val avatarParam = itemView.item_comment_img_avatar.layoutParams
            val size = Util.convertDpToPx(itemView.context, 36)
            avatarParam.width = size
            avatarParam.height = size
            itemView.item_comment_img_avatar.layoutParams = avatarParam

            itemView.item_comment_img_avatar.setOnClickListener {
                listener?.onViewProfileDetail(comment.user_Id)
            }

            itemView.item_comment_tv_username.setOnClickListener {
                listener?.onViewProfileDetail(comment.user_Id)
            }

            itemView.item_comment_tv_reply.setOnClickListener {
                listener?.onReplyComment(comment)
            }
        }
    }
}