package com.moban.flow.newprojectdetail.news

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.google.android.youtube.player.YouTubePlayer
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.feed.NewFeedAdapter
import com.moban.constant.Constant
import com.moban.constant.ServerURL
import com.moban.enum.NewPostQuickAction
import com.moban.enum.SearchForType
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.flow.editpost.EditPostActivity
import com.moban.flow.newpost.NewPostActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.flow.youtube.YoutubeVideoFragment
import com.moban.handler.FeedItemHandler
import com.moban.handler.FeedItemHeaderItemHandler
import com.moban.handler.IYoutubeContainer
import com.moban.handler.LoadMoreHandler
import com.moban.helper.LocalStorage
import com.moban.helper.ShareHelper
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.data.project.Project
import com.moban.model.response.PhotoResponse
import com.moban.model.response.SimpleResponse
import com.moban.model.response.post.PostResponse
import com.moban.model.response.post.PostStatusResponse
import com.moban.mvp.BaseMvpActivity
import com.moban.network.service.ImageService
import com.moban.network.service.PostService
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_project_news_detail.*
import kotlinx.android.synthetic.main.nav.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectNewActivity : BaseMvpActivity<IProjectNewsView, IProjectNewPresenter>(), IYoutubeContainer, IProjectNewsView {
    override var presenter: IProjectNewPresenter = ProjectNewPresenterIml()
    private var lowestId :Int? = null
    private var feedAdapter =  NewFeedAdapter()
    private var noNetworkDialog: Dialog? = null
    private var dialog: Dialog? = null
    private var flVideoContainer: FrameLayout? = null
    private var youtubeVideoFragment: YoutubeVideoFragment? = null
    private var viewYoutubePos = -1
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val postService = retrofit?.create(PostService::class.java)
    private val photoService = retrofit?.create(ImageService::class.java)
    private lateinit var project: Project
    private var type: Int = 0

    private var scrollRequireReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        }
    }
    companion object {
        const val BROADCAST_SCROLL_REQUEST = "BROADCAST_SCROLL_REQUEST"
        const val BUNDLE_SCROLL_REQUEST_VALUE = "BUNDLE_SCROLL_REQUEST_VALUE"
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        const val BUNDLE_KEY_TYPE = "BUNDLE_KEY_TYPE"

        fun start(context: Context, project: Project, type: Int) {
            val intent = Intent(context, ProjectNewActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            bundle.putInt(BUNDLE_KEY_TYPE, type)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                .registerReceiver(scrollRequireReceiver, IntentFilter(BROADCAST_SCROLL_REQUEST))
    }

    override fun onPause() {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).unregisterReceiver(scrollRequireReceiver)
        super.onPause()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_project_news_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        project_news_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
            type = bundle.getInt(BUNDLE_KEY_TYPE)
            if (type == 0) {
                project_news_tbToolbar.nav_tvTitle.text = "Tin Tức"
            } else {
                project_news_tbToolbar.nav_tvTitle.text = "Đánh Giá"
            }
        }

        initRecycleView()
        refreshData()
    }

    private fun refreshData() {
        lowestId = null
        project_news_refresh_layout.isRefreshing = true
        if (type == 0) {
            presenter.loadPost(project.id, lowestId)
        } else {
            presenter.loadReviewPost(project.id, lowestId)
        }
    }

    private fun initRecycleView() {
        project_news_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        project_news_refresh_layout.isRefreshing = true
        project_news_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        val linearLayoutManager = LinearLayoutManager(this)
        project_news_recycler_view.layoutManager = linearLayoutManager

        feedAdapter = initAdapter()

        feedAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                if (type == 0) {
                    presenter.loadPost(project.id, lowestId)
                } else {
                    presenter.loadReviewPost(project.id, lowestId)
                }
            }
        }
        project_news_recycler_view.adapter = feedAdapter
    }

    private fun initAdapter(): NewFeedAdapter {
        feedAdapter.removeHomeFeedItem()
        val context = this

        LocalStorage.user()?.let {
            feedAdapter.user = it
            feedAdapter.isCanPost = it.can_Post && type != 0
        }

        feedAdapter.listenerHeaderMenu = object: FeedItemHeaderItemHandler {
            override fun onBooked() {
                SearchProjectActivity.start(context, SearchForType.RESERVATION)
            }

            override fun onReward() {
                SearchProjectActivity.start(context, SearchForType.BOOKING)
            }

            override fun onTransaction() {
                val user = LocalStorage.user() ?: return
                ListReservationActivity.start(context, user)
            }

            override fun onProjects() {
                WebViewActivity.start(context, getString(R.string.service_title), ServerURL.serviceURL, oneTimeToken = true)
            }

            override fun onSecondaryProjects() {
                WebViewActivity.start(context, getString(R.string.event), ServerURL.eventURL, oneTimeToken = true)
            }
        }

        feedAdapter.listener = object : FeedItemHandler {
            override fun onReact(post: Post) {
                if (post.is_Liked) {
                    unlike(post)
                } else {
                    like(post)
                }
            }

            override fun onComment(post: Post) {
                startCommentPost(post)
            }

            override fun onViewFeed(post: Post) {
                viewPostDetail(post)
            }

            override fun onFollow(post: Post) {
                if (post.is_Followed) {
                    unfollow(post)
                } else {
                    follow(post)
                }
            }

            override fun onShare(post: Post, bitmaps: List<Bitmap>) {
                Util.saveToClipboard(this@ProjectNewActivity, post.getFullContent())
                ShareHelper.sharePost(this@ProjectNewActivity, post)
            }

            override fun onExpandContent(post: Post) {
                post.isExpanded = true
                feedAdapter.replacePostItem(post)
            }

            override fun onNewPost(quickAction: NewPostQuickAction) {
                NewPostActivity.start(this@ProjectNewActivity, quickAction)
            }

            override fun onViewProfileDetail(userId: Int) {
                AccountDetailActivity.start(this@ProjectNewActivity, userId)
            }

            override fun onDownloadPhoto(photo: com.moban.model.data.media.Photo) {
                downloadPhoto(photo)
            }

            override fun onClickURL(url: String) {
                WebViewActivity.start(this@ProjectNewActivity, getString(R.string.app_name), url)
            }

            override fun onDelete(post: Post) {
                deletePost(post)
            }

            override fun onEdit(post: Post) {
                EditPostActivity.start(this@ProjectNewActivity, this@ProjectNewActivity, post)
            }

            override fun onReplyComment(comment: Comment) {

            }
        }

        return feedAdapter
    }

    private fun downloadPhoto(photo: com.moban.model.data.media.Photo) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                downloadPhoto(photo)
            })
            return
        }

        if (!Permission.checkPermissionWriteExternalStorage(this)) {
            return
        }

        photoService?.download(photo.photo_Link)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.let {
                    val fileName = FileUtil.getFileNameFromURL(photo.photo_Link)
                    if (BitmapUtil.saveImage(this@ProjectNewActivity, it.byteStream(), fileName)) {
                        sendTracking(photo, Constant.NUM_RETRY_FAILING)
                    }
                }
            }
        })
    }

    /**
     * Like / unlike
     */

    private fun like(post: Post) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                like(post)
            })
            return
        }

        val postId = post.id

        postService?.like(postId)?.enqueue(object : Callback<PostStatusResponse> {
            override fun onFailure(call: Call<PostStatusResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostStatusResponse>?, response: Response<PostStatusResponse>?) {
                response?.body()?.let { it ->
                    if (it.success) {
                        post.is_Liked = true

                        it.data?.let {
                            post.likes = it.likes
                            post.comments = it.comments
                        }

                        feedAdapter.reloadPost(post)
                    }
                }
            }
        })
    }

    private fun unlike(post: Post) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                unlike(post)
            })

            return
        }

        val postId = post.id

        postService?.unlike(postId)?.enqueue(object : Callback<PostStatusResponse> {
            override fun onFailure(call: Call<PostStatusResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostStatusResponse>?, response: Response<PostStatusResponse>?) {
                response?.body()?.let { it ->
                    if (it.success) {
                        post.is_Liked = false

                        it.data?.let {
                            post.likes = it.likes
                            post.comments = it.comments
                        }

                        feedAdapter.reloadPost(post)
                    }
                }
            }
        })
    }

    /**
     * Follow
     */

    private fun follow(post: Post) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                follow(post)
            })
            return
        }

        val postId = post.id

        postService?.follow(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                response?.body()?.data?.let {
                    feedAdapter.reloadPost(it)

                    showMessagedFollowed()
                }
            }
        })
    }

    private fun unfollow(post: Post) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                unfollow(post)
            })
            return
        }

        val postId = post.id

        postService?.unfollow(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                response?.body()?.data?.let {
                    feedAdapter.reloadPost(it)
                }
            }
        })
    }

    private fun showMessagedFollowed() {
        DialogUtil.showInfoDialog(this,
                getString(R.string.feed_you_did_follow),
                getString(R.string.feed_followed_msg),
                getString(R.string.ok), null)
    }

    /**
     * Network dialog
     */

    private fun dismissNetworkDialog() {
        noNetworkDialog?.dismiss()
    }

    /**
     * Navigation
     */

    private fun viewPostDetail(post: Post) {
        PostDetailActivity.start(this, this, post, false)
    }

    private fun startCommentPost(post: Post) {
        PostDetailActivity.start(this, this, post, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LHApplication.instance.getFbManager()?.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.NEW_POST_REQUEST -> {
                data?.let { it ->
                    val newPost = it.getSerializableExtra(NewPostActivity.NEW_POST_KEY_DATA) as Post
                    feedAdapter.addFirstPost(newPost)
                }
            }
            Constant.EDIT_POST_REQUEST -> {
                data?.let {
                    val updatePost = it.getSerializableExtra(EditPostActivity.EDIT_POST_KEY_DATA) as Post
                    feedAdapter.reloadPost(updatePost)
                }
            }
            Constant.DETAIL_POST_REQUEST -> {
                data?.let {
                    val updatePost = it.getSerializableExtra(PostDetailActivity.BUNDLE_KEY_POST) as Post
                    feedAdapter.reloadPost(updatePost)
                }
            }
        }
    }

    private fun sendTracking(photo: com.moban.model.data.media.Photo, numOfRetry: Int) {
        if (!NetworkUtil.hasConnection(this)) {
            return
        }
        if (numOfRetry == 0) {
            return
        }

        photoService?.trackSave(photo.id)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                if (response?.body() == null) {
                    sendTracking(photo, numOfRetry - 1)
                }

                response?.body()?.let {
                    if (!it.success) {
                        sendTracking(photo, numOfRetry - 1)
                    }
                }
            }
        })
    }

    private fun deletePost(post: Post) {
        if (!NetworkUtil.hasConnection(this)) {
            noNetworkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                deletePost(post)
            })
            return
        }

        dialog = DialogUtil.showConfirmDialog(this, true, getString(R.string.delete_post), getString(R.string.delete_post_comfirm),
                getString(R.string.delete), getString(R.string.skip),
                View.OnClickListener {
                    postService?.delete(post.id)?.enqueue(object : Callback<SimpleResponse> {
                        override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                            DialogUtil.showErrorDialog(this@ProjectNewActivity, getString(R.string.delete_post_error_title),
                                    getString(R.string.check_in_fail_desc), getString(R.string.ok), null)
                        }

                        override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                            response?.body()?.let {
                                dialog?.dismiss()
                                if (it.success) {
                                    feedAdapter.removePost(post)
                                } else {
                                    DialogUtil.showErrorDialog(this@ProjectNewActivity, getString(R.string.delete_post_error_title),
                                            getString(R.string.check_in_fail_desc), getString(R.string.ok), null)
                                }
                            }
                        }
                    })
                }, View.OnClickListener {
            dialog?.dismiss()
        })
    }

    private fun sharePost(post: Post, bitmaps: List<Bitmap>) {
        if (!AppUtil.hasFacebookApp(this)) {
            dialog = DialogUtil.showErrorDialog(this,
                    "Bạn chưa có Facebook app",
                    "Bạn hãy cài đặt Facebook app để chia sẻ bài viết nhé.",
                    getString(R.string.ok), View.OnClickListener {
                dialog?.dismiss()
                AppUtil.openAppPackage(this, Constant.FACEBOOK_APP_PACKAGE_NAME)
            })
            return
        }

        if (post.hasVideo()) {
            val link = "https://www.youtube.com/watch?v=" + post.youtubeId

            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(link))
                    .build()

            val dialog = ShareDialog(this)
            initCallbackShareFacebook(dialog, post)
            dialog.show(linkContent, ShareDialog.Mode.AUTOMATIC)
        } else {
            val content = SharePhotoContent.Builder()
            bitmaps.forEach {
                val photo = SharePhoto.Builder().setBitmap(it).build()
                content.addPhoto(photo)
            }

            val shareDialog = ShareDialog(this)
            initCallbackShareFacebook(shareDialog, post)
            shareDialog.show(content.build(), ShareDialog.Mode.NATIVE)
        }
    }

    private fun initCallbackShareFacebook(shareDialog: ShareDialog, post: Post) {
        shareDialog.registerCallback(LHApplication.instance.getFbManager(), object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result?) {
                trackShare(post.id, Constant.NUM_RETRY_FAILING)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })
    }

    private fun trackShare(postId: Int, numOfRetry: Int) {
        if (!NetworkUtil.hasConnection(this)) {
            return
        }

        if (numOfRetry == 0) {
            return
        }

        postService?.trackShare(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                if (response?.body() == null) {
                    trackShare(postId, numOfRetry - 1)
                }

                response?.body()?.let {
                    if (!it.success) {
                        trackShare(postId, numOfRetry - 1)
                    }
                }
            }
        })
    }

    // YouTube video
    override fun sendLoggingEvent(action: String) {
    }

    private fun initVideoFrameLayout(youtubeVideoFragment: YoutubeVideoFragment) {
        fragmentManager.beginTransaction()
                .replace(getFlVideoContainer().id, youtubeVideoFragment,
                        YoutubeVideoFragment.TAG)
                .commitAllowingStateLoss()
    }

    override fun getFlVideoContainer(): FrameLayout {
        if (flVideoContainer == null) {
            flVideoContainer = FrameLayout(this)

            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            flVideoContainer?.layoutParams = layoutParams
            flVideoContainer?.visibility = View.GONE
            flVideoContainer?.id = View.generateViewId()
        }

        return flVideoContainer!!
    }

    override fun getYoutubeVideoFragment(): YoutubeVideoFragment {
        if (youtubeVideoFragment == null) {
            youtubeVideoFragment = YoutubeVideoFragment.newInstance()
            youtubeVideoFragment?.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
            youtubeVideoFragment?.setShowFullscreenButton(false)

            initVideoFrameLayout(youtubeVideoFragment!!)
        }

        return youtubeVideoFragment!!
    }

    override fun getViewYoutubePos(): Int {
        return viewYoutubePos
    }

    override fun setViewYoutubePos(position: Int) {
        viewYoutubePos = position
    }

    override fun handleOnVideoFullScreen(isFullScreen: Boolean) {
    }

    override fun checkYouTubeViewOverlay() {
        val playerLocation = IntArray(2)
        getFlVideoContainer().getLocationInWindow(playerLocation)
//        val x = playerLocation[0]
        val y = playerLocation[1]
        val pagerLocation = IntArray(2)
        project_news_refresh_layout.getLocationOnScreen(pagerLocation)
        val relativeY = y - pagerLocation[1]
        val topBarHeight = 45

        if (relativeY < topBarHeight) {
            val intent = Intent(BROADCAST_SCROLL_REQUEST)
            intent.putExtra(BUNDLE_SCROLL_REQUEST_VALUE, relativeY - topBarHeight)
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else if (relativeY + getFlVideoContainer().height > project_news_refresh_layout.height) {
            val intent = Intent(BROADCAST_SCROLL_REQUEST)
            val value = relativeY + getFlVideoContainer().height - project_news_refresh_layout.height
            intent.putExtra(BUNDLE_SCROLL_REQUEST_VALUE, value)
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    override fun releaseYouTubePlayer() {
        youtubeVideoFragment?.releasePlayer()

        youtubeVideoFragment = null
    }

    override fun bindingPosts(posts: List<Post>, lowestId: Int, canLoadMore: Boolean) {
        //TODO: Ignore canLoadMore
        val canLoadMore = false
        this.lowestId = lowestId
        if (feedAdapter.isDataEmpty) {
            feedAdapter.setPosts(posts, canLoadMore)
        } else {
            feedAdapter.appendPosts(posts, canLoadMore)
        }
        project_news_refresh_layout.isRefreshing = false
    }

    override fun finishLoading() {
        project_news_refresh_layout.isRefreshing = false
    }
}
