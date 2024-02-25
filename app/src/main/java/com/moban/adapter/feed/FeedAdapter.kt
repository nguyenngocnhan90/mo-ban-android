package com.moban.adapter.feed

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.enum.NewPostQuickAction
import com.moban.extension.toInt
import com.moban.handler.*
import com.moban.model.data.post.Post
import com.moban.model.data.user.User
import com.moban.view.viewholder.*
import kotlinx.android.synthetic.main.item_feed.view.*
import kotlinx.android.synthetic.main.item_feed_new_post.view.*
import kotlinx.android.synthetic.main.item_feed_product_type.view.*

/**
 * Created by neal on 3/4/18.
 */

class FeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FEED = 0
        private const val TYPE_LOAD_MORE = 1
        private const val TYPE_NEW_POST = 2
        private const val TYPE_FEED_BIRTHDAY = 3
        private const val TYPE_FEED_RANK = 4
        private const val TYPE_FEED_DEAL = 6
        private const val TYPE_FEED_USER = 7
        private const val TYPE_FEED_PRODUCT_TYPE = 8
        private const val TYPE_FEED_HIGHLIGHT_PROJECT = 9
    }

    var listener: FeedItemHandler? = null

    var listenerSecondary: FeedProjectTypeItemHandler? = null

    var user: User? = null
    var isCanPost = false
    var isShowProductType = false
    var isShowHighlightProject = false

    var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    private var posts: MutableList<Post> = ArrayList()
    val isDataEmpty: Boolean
        get() = posts.isEmpty()

    private fun getLayoutResourceId(viewType: Int): Int {
        return arrayOf(
                R.layout.item_feed,
                R.layout.item_load_more,
                R.layout.item_feed_new_post,
                R.layout.item_feed_birthday,
                R.layout.item_feed_rank,
                R.layout.item_feed,
                R.layout.item_feed_deals,
                R.layout.item_feed_new_partner,
                R.layout.item_feed_product_type,
                R.layout.item_feed_highlight_project
        )[viewType]
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && isShowProductType) {
            return TYPE_FEED_PRODUCT_TYPE
        }
        val positionCanPost = if (isShowProductType) 1 else 0

        if ((position == positionCanPost && isCanPost)) {
            return TYPE_NEW_POST
        }

        if (isShowHighlightProject && position == positionCanPost + 1) {
            return TYPE_FEED_HIGHLIGHT_PROJECT
        }

        val postPosition = position - isCanPost.toInt - isShowProductType.toInt - isShowHighlightProject.toInt
        if (postPosition < posts.size && postPosition >= 0) {
            val post = posts[postPosition]
            return when (post.type) {
                0 -> TYPE_FEED
                1 -> TYPE_FEED_BIRTHDAY
                2 -> TYPE_FEED_RANK
                4 -> TYPE_FEED_DEAL
                5 -> TYPE_FEED_USER
                else -> TYPE_FEED
            }
        }
        return TYPE_LOAD_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType),
                parent,
                false)

        return when (viewType) {
            TYPE_NEW_POST -> NewPostItemViewHolder(view)
            TYPE_LOAD_MORE -> LoadMoreViewHolder(view)
            TYPE_FEED_BIRTHDAY -> FeedBirthdayItemViewHolder(view)
            TYPE_FEED_USER -> FeedNewPartnerItemViewHolder(view)
            TYPE_FEED_DEAL -> FeedDealItemViewHolder(view)
            TYPE_FEED_RANK -> FeedRankItemViewHolder(view)
            TYPE_FEED_PRODUCT_TYPE -> FeedProductTypeItemViewHolder(view)
            TYPE_FEED_HIGHLIGHT_PROJECT -> FeedHighlightProjectItemViewHolder(view)
            else -> FeedItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return posts.count() + isLoadMoreAvailable.toInt + isCanPost.toInt + isShowProductType.toInt + isShowHighlightProject.toInt
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val postPosition = position - isCanPost.toInt - isShowProductType.toInt - isShowHighlightProject.toInt

        when (holder.itemViewType) {
            TYPE_FEED_PRODUCT_TYPE -> {
                val holderProjectType = holder as FeedProductTypeItemViewHolder
                holderProjectType.bind()
                val itemView = holder.itemView

                arrayOf(itemView.item_feed_product_type_view, itemView.item_feed_product_type_img, itemView.item_feed_product_type_tv).forEach {
                    it.setOnClickListener {
                        listenerSecondary?.onSecondary()
                    }
                }

                arrayOf(itemView.item_feed_product_type_view_event, itemView.item_feed_product_type_img_event, itemView.item_feed_product_type_tv_event).forEach {
                    it.setOnClickListener {
                        listenerSecondary?.onEvent()
                    }
                }

                arrayOf(itemView.item_feed_product_type_view_service, itemView.item_feed_product_type_img_service, itemView.item_feed_product_type_tv_service).forEach {
                    it.setOnClickListener {
                        listenerSecondary?.onService()
                    }
                }

                arrayOf(itemView.item_feed_view_linkhub, itemView.item_feed_img_linkhub, itemView.item_feed_tv_linkhub).forEach {
                    it.setOnClickListener {
                        listenerSecondary?.onLinkHub()
                    }
                }
            }
            TYPE_NEW_POST -> {
                val viewHolder = holder as NewPostItemViewHolder
                val itemView = holder.itemView

                user?.let { it ->
                    viewHolder.bind(it)

                    itemView.setOnClickListener {
                        listener?.onNewPost(NewPostQuickAction.NONE)
                    }

                    itemView.home_new_post_add_image_content.setOnClickListener {
                        listener?.onNewPost(NewPostQuickAction.PHOTO)
                    }

                    itemView.home_new_post_add_youtube_link.setOnClickListener {
                        listener?.onNewPost(NewPostQuickAction.YOUTUBE_LINK)
                    }

                    itemView.home_new_post_add_topic.setOnClickListener {
                        listener?.onNewPost(NewPostQuickAction.TOPIC)
                    }
                }
            }

            TYPE_LOAD_MORE -> {
                if (!isLoading && loadMoreHandler != null && postPosition != -1) {
                    isLoading = true
                    loadMoreHandler?.onLoadMore()
                }
            }

            TYPE_FEED -> {
                bindingNewFeed(holder, postPosition)
            }
            TYPE_FEED_BIRTHDAY -> {
                bindingBirthdayFeed(holder, postPosition)
            }
            TYPE_FEED_USER -> {
                bindingNewPartnerFeed(holder, postPosition)
            }
            TYPE_FEED_DEAL -> {
                bindingDealFeed(holder, postPosition)
            }
            TYPE_FEED_RANK -> {
                bindingRankFeed(holder, postPosition)
            }
            TYPE_FEED_HIGHLIGHT_PROJECT -> {
                val highlightHolder = holder as FeedHighlightProjectItemViewHolder
                highlightHolder.bind()
            }
        }
    }

    private fun bindingRankFeed(holder: RecyclerView.ViewHolder, postPosition: Int) {
        val viewHolder = holder as FeedRankItemViewHolder
        if (postPosition < posts.count()) {
            val post = posts[postPosition]
            viewHolder.bind(post)
            viewHolder.listener = object : UserItemHandler {
                override fun onClicked(user: User) {
                    listener?.onViewProfileDetail(user.id)
                }
            }
        }
    }

    private fun bindingDealFeed(holder: RecyclerView.ViewHolder, postPosition: Int) {
        val viewHolder = holder as FeedDealItemViewHolder
        if (postPosition < posts.count()) {
            val post = posts[postPosition]
            viewHolder.bind(post, null)
        }
    }

    private fun bindingNewPartnerFeed(holder: RecyclerView.ViewHolder, postPosition: Int) {
        val viewHolder = holder as FeedNewPartnerItemViewHolder
        if (postPosition < posts.count()) {
            val post = posts[postPosition]
            viewHolder.bind(post)
            viewHolder.listener = object : UserItemHandler {
                override fun onClicked(user: User) {
                    listener?.onViewProfileDetail(user.id)
                }
            }
        }
    }

    private fun bindingBirthdayFeed(holder: RecyclerView.ViewHolder, postPosition: Int) {
        val viewHolder = holder as FeedBirthdayItemViewHolder
        if (postPosition < posts.count()) {
            val post = posts[postPosition]
            viewHolder.bind(post)
            viewHolder.listener = object : UserItemHandler {
                override fun onClicked(user: User) {
                    listener?.onViewProfileDetail(user.id)
                }
            }
        }
    }

    private fun bindingNewFeed(holder: RecyclerView.ViewHolder, postPosition: Int) {
        val viewHolder = holder as FeedItemViewHolder

        if (postPosition < posts.count()) {
            val post = posts[postPosition]
            viewHolder.bind(post, postPosition, listener)

            val itemView = holder.itemView

            val viewDetailViews = arrayOf(
                    itemView.item_feed_lltAccountAndBox,
                    itemView.item_feed_rltReactInfo)
            viewDetailViews.forEachIndexed { _, view ->
                view.setOnClickListener {
                    listener?.onViewFeed(post)
                }
            }

            itemView.item_feed_llLike.setOnClickListener {
                listener?.onReact(post)
            }

            itemView.item_feed_llComment.setOnClickListener {
                listener?.onComment(post)
            }

            itemView.item_feed_llShare.setOnClickListener { _ ->
                val photos = post.photos
                var imageViews = arrayOf(itemView.item_feed_list_image_1,
                        itemView.item_feed_list_image_2,
                        itemView.item_feed_list_image_3)
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

                listener?.onShare(post, images)
            }

            // Spoiler text view handler
            itemView.item_feed_tv_content.setSpoilerTextViewHandler {
                listener?.onExpandContent(post)
            }

            itemView.item_feed_img_avatar.setOnClickListener {
                listener?.onViewProfileDetail(post.user_Id)
            }
        }
    }

    private fun notifyDataChanged() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setPosts(posts: List<Post>, isLoadMoreAvailable: Boolean) {
        this.posts = posts.toMutableList()
        this.isLoadMoreAvailable = isLoadMoreAvailable

        notifyDataChanged()
    }

    fun appendPosts(posts: List<Post>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.posts.size + isCanPost.toInt + isShowProductType.toInt + isShowHighlightProject.toInt
        val length = posts.size + isLoadMoreAvailable.toInt

        this.posts.addAll(posts)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun reloadPost(post: Post) {
        val index = posts.indexOfFirst { p -> p.id == post.id }
        val realIndex = index + isCanPost.toInt + isShowProductType.toInt + isShowHighlightProject.toInt

        if (index >= 0 && index < posts.count()) {
            val currentPost = posts[index]
            post.isExpanded = currentPost.isExpanded

            posts[index] = post
            notifyItemChanged(realIndex)
        }
    }

    fun removePost(post: Post) {
        val index = posts.indexOfFirst { p -> p.id == post.id }
        if (index >= 0 && index < posts.count()) {
            posts.removeAt(index)
            this.notifyDataChanged()
        }
    }

    fun replacePostItem(post: Post) {
        val index = posts.indexOfFirst { p -> p.id == post.id }

        if (index >= 0 && index < posts.count()) {
            posts[index] = post
        }
    }

    fun addFirstPost(post: Post) {
        posts.add(0, post)
        notifyDataChanged()
    }

    fun bindingHighlightsProject() {
        val posHighlight = isCanPost.toInt + 1  //0: Project type =>1:  Post status => 2: High light project
        if (isShowHighlightProject) {
            notifyItemChanged(posHighlight)
        }
    }
}
