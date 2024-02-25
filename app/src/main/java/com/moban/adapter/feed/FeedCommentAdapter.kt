package com.moban.adapter.feed

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.extension.toInt
import com.moban.handler.FeedCommentItemHandler
import com.moban.handler.FeedItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.view.viewholder.CommentItemViewHolder
import com.moban.view.viewholder.FeedItemViewHolder
import com.moban.view.viewholder.LoadMoreCommentItemViewHolder
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_feed.view.*

/**
 * Created by neal on 3/13/18.
 */
class FeedCommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        private const val TYPE_FEED = 0
        private const val TYPE_COMMENT = 1
        private const val TYPE_LOAD_MORE = 2
    }

    var post: Post? = null
        set(value) {
            field = value
            notifyItemChanged(0)
            totalCommentCount = value?.comments ?: 0
        }

    var totalCommentCount: Int = 0
    var unloadCommentCount: Int = 0
    var isLoadMoreAvailable: Boolean = false

    var comments: List<Comment> = ArrayList()
        set(value) {
            field = value

            var commentCount = 0
            for (item in field) {
                commentCount += (1 + item.comment_Count)
            }

            unloadCommentCount = totalCommentCount - commentCount
            isLoadMoreAvailable = unloadCommentCount > 0
        }

    var listener: FeedItemHandler? = null
    var loadMoreHandler: LoadMoreHandler? = null

    private fun getLayoutResourceId(viewType: Int): Int {
        return when (viewType) {
            TYPE_COMMENT -> R.layout.item_comment
            TYPE_LOAD_MORE -> R.layout.item_load_more_comment
            else -> R.layout.item_feed
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_FEED
        }

        if (position == 1 && isLoadMoreAvailable) {
            return TYPE_LOAD_MORE
        }

        return TYPE_COMMENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            getLayoutResourceId(viewType),
            parent,
            false
        )

        return when (viewType) {
            TYPE_COMMENT -> {
                CommentItemViewHolder(view)
            }

            TYPE_LOAD_MORE -> {
                LoadMoreCommentItemViewHolder(view)
            }

            else -> {
                FeedItemViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return 1 + isLoadMoreAvailable.toInt + comments.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_FEED -> {
                val viewHolder = holder as FeedItemViewHolder
                post?.let { it ->
                    val currentPost = it
                    currentPost.isExpanded = true
                    viewHolder.bind(currentPost, position, listener)

                    val itemView = holder.itemView

                    val viewDetailViews = arrayOf(
                        itemView.item_feed_lltAccountAndBox,
                        itemView.item_feed_rltReactInfo
                    )
                    viewDetailViews.forEachIndexed { _, view ->
                        view.setOnClickListener {
                            listener?.onViewFeed(currentPost)
                        }
                    }

                    itemView.item_feed_llLike.setOnClickListener {
                        listener?.onReact(currentPost)
                    }

                    itemView.item_feed_llComment.setOnClickListener {
                        listener?.onComment(currentPost)
                    }

                    itemView.item_feed_llShare.setOnClickListener { _ ->
                        val photos = currentPost.photos
                        var imageViews = arrayOf(
                            itemView.item_feed_list_image_1,
                            itemView.item_feed_list_image_2,
                            itemView.item_feed_list_image_3
                        )
                        if (photos.size == 1) {
                            imageViews = arrayOf(itemView.item_feed_main_image)
                        }

                        val images: MutableList<Bitmap> = ArrayList()
                        photos.forEachIndexed { index, _ ->
                            if (index < imageViews.size) {
                                val imageView = imageViews[index].drawable
                                imageView?.let {
                                    if (it is BitmapDrawable) {
                                        images.add(it.bitmap)
                                    }
                                }
                            }
                        }

                        listener?.onShare(currentPost, images)
                    }

                    // Spoiler text view handler
                    itemView.item_feed_tv_content.setSpoilerTextViewHandler {
                        listener?.onExpandContent(currentPost)
                    }

                    itemView.item_feed_img_avatar.setOnClickListener {
                        listener?.onViewProfileDetail(currentPost.user_Id)
                    }

                    itemView.item_feed_tv_user.setOnClickListener {
                        listener?.onViewProfileDetail(currentPost.user_Id)
                    }
                }
            }

            TYPE_COMMENT -> {
                val viewHolder = holder as CommentItemViewHolder

                val idx = position - 1
                if (idx < comments.count()) {
                    val currentComment = comments[idx]
                    viewHolder.bind(currentComment)

                    val itemView = holder.itemView
                    itemView.item_comment_img_avatar.setOnClickListener {
                        listener?.onViewProfileDetail(currentComment.user_Id)
                    }

                    itemView.item_comment_tv_username.setOnClickListener {
                        listener?.onViewProfileDetail(currentComment.user_Id)
                    }

                    itemView.item_comment_tv_reply.setOnClickListener {
                        listener?.onReplyComment(currentComment)
                    }

                    viewHolder.subCommentAdapter.listener = object : FeedCommentItemHandler {
                        override fun onReplyComment(comment: Comment) {
                            listener?.onReplyComment(currentComment)
                        }

                        override fun onViewProfileDetail(userId: Int) {
                            listener?.onViewProfileDetail(currentComment.user_Id)
                        }
                    }
                }
            }

            TYPE_LOAD_MORE -> {
                val viewHolder = holder as LoadMoreCommentItemViewHolder
                viewHolder.bind(unloadCommentCount)

                viewHolder.itemView.setOnClickListener {
                    loadMoreHandler?.onLoadMore()
                }
            }
        }
    }

    fun appendComments(comments: List<Comment>) {
//        val from = this.comments.size
        val length = comments.size

        this.comments = comments + this.comments

        notifyItemRangeChanged(1 + isLoadMoreAvailable.toInt, length)
        if (isLoadMoreAvailable) {
            notifyItemChanged(1)
        }
    }

    fun appendMyComment(comment: Comment) {
        comments += comment
        notifyItemChanged(comments.count())
    }

    fun reloadSubComments(parentComment: Comment) {
        comments = comments.map {
            var newComment = it
            if (it.id == parentComment.id) {
                newComment = parentComment
            }

            newComment
        }

        comments.indexOfFirst {
            it.id == parentComment.id
        }.let {
            notifyItemChanged(it + isLoadMoreAvailable.toInt + 1)
        }
    }
}
