package com.moban.adapter.feed

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.enum.NewPostQuickAction
import com.moban.enum.ProjectHighlightType
import com.moban.extension.toInt
import com.moban.handler.*
import com.moban.helper.LocalStorage
import com.moban.model.data.Photo
import com.moban.model.data.homedeal.HomeDeal
import com.moban.model.data.post.Post
import com.moban.model.data.project.Project
import com.moban.model.data.statistic.Statistic
import com.moban.model.data.user.User
import com.moban.view.viewholder.*
import kotlinx.android.synthetic.main.item_feed.view.*
import kotlinx.android.synthetic.main.item_feed_new_post.view.*
import kotlinx.android.synthetic.main.item_feed_top_menu.view.*

/**
 * Created by neal on 3/4/18.
 */

class NewFeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FEED_BANNERS_INDEX = 2
        private const val TYPE_FEED_NEW_DEALS_INDEX = 3
        private const val TYPE_FEED_STATISTICS_INDEX = 4
        private const val TYPE_FEED_HOT_PROJECT_INDEX = 5
        private const val TYPE_FEED_RECENT_VIEW_PROJECT_INDEX = 6
        private const val TYPE_FEED_TOP_PARTNER_INDEX = 7

        private const val TYPE_FEED = 0
        private const val TYPE_LOAD_MORE = 1
        private const val TYPE_NEW_POST = 2
        private const val TYPE_FEED_BIRTHDAY = 3
        private const val TYPE_FEED_RANK = 4
        private const val TYPE_FEED_DEAL = 6
        private const val TYPE_FEED_USER = 7
        private const val TYPE_FEED_PRODUCT_TYPE = 8
        private const val TYPE_FEED_BANNERS = 9
        private const val TYPE_FEED_NEW_DEALS = 10
        private const val TYPE_FEED_STATISTICS = 11
        private const val TYPE_FEED_HOT_PROJECT = 12
        private const val TYPE_FEED_RECENT_VIEW = 13
        private const val TYPE_FEED_TOP_PARTNER = 14
        private const val TYPE_FEED_WELCOME = 15
    }

    private var arrayFixedItemType: MutableList<Int> = arrayOf(TYPE_FEED_WELCOME, TYPE_FEED_BANNERS, TYPE_FEED_NEW_DEALS, TYPE_FEED_STATISTICS, TYPE_FEED_HOT_PROJECT, TYPE_FEED_RECENT_VIEW, TYPE_FEED_TOP_PARTNER).toMutableList()
    var listener: FeedItemHandler? = null
    var listenerHeaderMenu: FeedItemHeaderItemHandler? = null
    var listenerWelcome: FeedWelcomeItemHandler? = null

    var user: User? = null
    var isCanPost = false
    var isShowHighlightProject = false

    var isLoadMoreAvailable = false
    private var isLoading = false
    var loadMoreHandler: LoadMoreHandler? = null

    private var posts: MutableList<Post> = ArrayList()
    val isDataEmpty: Boolean
        get() = posts.isEmpty()

    var banners: MutableList<Photo> = ArrayList()
    var hightlightProjects: MutableList<Project> = ArrayList()
    var recentProjects: MutableList<Project> = ArrayList()
    var homeDeals: MutableList<HomeDeal> = ArrayList()
    var topPartner: MutableList<User> = ArrayList()
    var statistics: MutableList<Statistic> = ArrayList()

    fun removeHomeFeedItem() {
        arrayFixedItemType.clear()
    }

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
                R.layout.item_feed_top_menu,
                R.layout.item_feed_banner,
                R.layout.item_feed_new_deals,
                R.layout.item_feed_statistics,
                R.layout.item_feed_hot_product,
                R.layout.item_feed_hot_product,
                R.layout.item_feed_top_partner,
                R.layout.item_feed_welcome
        )[viewType]
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && isCanPost) {
            return TYPE_NEW_POST
        }

        val fixedTypeIndex = position - isCanPost.toInt
        if (fixedTypeIndex < arrayFixedItemType.size) {
            return arrayFixedItemType[fixedTypeIndex]
        }

        val postPosition = position - isCanPost.toInt - arrayFixedItemType.size
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
            TYPE_FEED_WELCOME -> FeedWelcomeItemViewHolder(view)
            TYPE_FEED_PRODUCT_TYPE -> FeedProductTypeItemViewHolder(view)
            TYPE_FEED_BANNERS -> FeedBannerItemViewHolder(view)
            TYPE_FEED_NEW_DEALS -> FeedHomeDealItemViewHolder(view)
            TYPE_FEED_STATISTICS -> FeedStatistictemViewHolder(view)
            TYPE_FEED_HOT_PROJECT -> FeedHotProjectItemViewHolder(view)
            TYPE_FEED_RECENT_VIEW -> FeedHotProjectItemViewHolder(view)
            TYPE_FEED_TOP_PARTNER -> TopPartnerItemViewHolder(view)
            else -> FeedItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return posts.count() + isCanPost.toInt + arrayFixedItemType.size + isLoadMoreAvailable.toInt
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val postPosition = position - isCanPost.toInt - arrayFixedItemType.size

        when (holder.itemViewType) {
            TYPE_FEED_PRODUCT_TYPE -> {
                val holderProjectType = holder as FeedProductTypeItemViewHolder
                holderProjectType.bind()
                val itemView = holder.itemView

                arrayOf(itemView.item_feed_product_book, itemView.item_feed_product_book_img).forEach {
                    it.setOnClickListener {
                        listenerHeaderMenu?.onBooked()
                    }
                }

                arrayOf(itemView.item_feed_reward, itemView.item_feed_reward_img).forEach {
                    it.setOnClickListener {
                        listenerHeaderMenu?.onReward()
                    }
                }

                arrayOf(itemView.item_feed_product_transaction, itemView.item_feed_product_transaction_img).forEach {
                    it.setOnClickListener {
                        listenerHeaderMenu?.onTransaction()
                    }
                }

                arrayOf(itemView.item_feed_so_cap, itemView.item_feed_so_cap_img).forEach {
                    it.setOnClickListener {
                        listenerHeaderMenu?.onProjects()
                    }
                }

                arrayOf(itemView.item_feed_thu_cap, itemView.item_feed_thu_cap_img).forEach {
                    it.setOnClickListener {
                        listenerHeaderMenu?.onSecondaryProjects()
                    }
                }
            }

            TYPE_FEED_WELCOME -> {
                (holder as? FeedWelcomeItemViewHolder)?.let {
                    it.bind()

                    val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
                    if (isAnonymous) {
                        it.itemView.setOnClickListener {
                            listenerWelcome?.onLogin()
                        }
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
            TYPE_FEED_BANNERS -> {
                val bannerFeeds = holder as FeedBannerItemViewHolder
                bannerFeeds.bind(banners)
            }
            TYPE_FEED_NEW_DEALS -> {
                val itemView = holder as FeedHomeDealItemViewHolder
                itemView.bind(homeDeals)
            }
            TYPE_FEED_HOT_PROJECT -> {
                if (hightlightProjects.isNotEmpty()) {
                    val itemView = holder as FeedHotProjectItemViewHolder
                    itemView.bind(hightlightProjects, ProjectHighlightType.NONE)
                }
            }
            TYPE_FEED_RECENT_VIEW -> {
                if (recentProjects.isNotEmpty()) {
                    val itemView = holder as FeedHotProjectItemViewHolder
                    itemView.bind(recentProjects, ProjectHighlightType.RECENT)
                }
            }
            TYPE_FEED_STATISTICS -> {
                val itemView = holder as FeedStatistictemViewHolder
                itemView.bind(statistics)
            }
            TYPE_FEED_TOP_PARTNER -> {
                if (topPartner.isNotEmpty()) {
                    val itemView = holder as TopPartnerItemViewHolder
                    itemView.bind(topPartner)
                }
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
        Log.i("TAG DEBUG: ", "Binding new feed $postPosition on post count: ${posts.size}")
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

        val from = arrayFixedItemType.size
        val length = posts.size + isLoadMoreAvailable.toInt
        notifyItemRangeChanged(from, length)
    }

    fun appendPosts(posts: List<Post>, isLoadMoreAvailable: Boolean) {
        this.isLoadMoreAvailable = isLoadMoreAvailable

        val from = this.posts.size + arrayFixedItemType.size
        val length = posts.size + isLoadMoreAvailable.toInt

        this.posts.addAll(posts)

        isLoading = false
        notifyItemRangeChanged(from, length)
    }

    fun reloadPost(post: Post) {
        val index = posts.indexOfFirst { p -> p.id == post.id }
        val realIndex = index + arrayFixedItemType.size

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

    fun bindingHighlightsProject(projects: List<Project>) {
        if (projects.isEmpty()) {
            removeIndex(TYPE_FEED_HOT_PROJECT_INDEX)
        } else {
            hightlightProjects = projects.toMutableList()
            notifyItemChanged(TYPE_FEED_HOT_PROJECT_INDEX)
        }
    }

    fun bindingRecentProject(projects: List<Project>) {
        if (projects.isEmpty()) {
            removeIndex(TYPE_FEED_RECENT_VIEW_PROJECT_INDEX)
        } else {
            recentProjects = projects.toMutableList()
            notifyItemChanged(TYPE_FEED_RECENT_VIEW_PROJECT_INDEX)
        }
    }

    fun bindingBanners(banners: List<Photo>) {
        if (banners.isEmpty()) {
            removeIndex(TYPE_FEED_BANNERS_INDEX)
        } else {
            this.banners = banners.toMutableList()
            notifyItemChanged(TYPE_FEED_BANNERS_INDEX)
        }
    }

    private fun removeIndex(index: Int) {
        if (index < arrayFixedItemType.size) {
            arrayFixedItemType.removeAt(index)
            notifyDataChanged()
        }
    }

    fun bindNewDealsProject(deals: List<HomeDeal>) {
        if (deals.isEmpty()) {
            removeIndex(TYPE_FEED_NEW_DEALS_INDEX)
        } else {
            homeDeals = deals.toMutableList()
            notifyDataChanged()
        }
    }

    fun bindStatistics(statistics: List<Statistic>) {
        if (statistics.isEmpty()) {
            removeIndex(TYPE_FEED_STATISTICS_INDEX)
        } else {
            this.statistics = statistics.toMutableList()
            notifyItemChanged(TYPE_FEED_STATISTICS_INDEX)
        }
    }

    fun bindTopPartner(users: List<User>) {
        if (users.isEmpty()) {
            removeIndex(TYPE_FEED_TOP_PARTNER_INDEX)
        } else {
            topPartner = users.toMutableList()
            notifyItemChanged(TYPE_FEED_TOP_PARTNER_INDEX)
        }
    }
}
