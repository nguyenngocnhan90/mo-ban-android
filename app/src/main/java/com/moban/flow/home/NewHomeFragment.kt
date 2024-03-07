package com.moban.flow.home

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.feed.NewFeedAdapter
import com.moban.constant.Constant
import com.moban.enum.LinkmartMainCategory
import com.moban.enum.NewPostQuickAction
import com.moban.enum.SearchForType
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.flow.editpost.EditPostActivity
import com.moban.flow.linkmart.LinkmartActivity
import com.moban.flow.main.MainActivity
import com.moban.flow.newpost.NewPostActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.secondary.project.SecondaryProjectsActivity
import com.moban.flow.signin.SignInActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.FeedItemHandler
import com.moban.handler.FeedItemHeaderItemHandler
import com.moban.handler.FeedWelcomeItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.helper.LocalStorage
import com.moban.helper.ShareHelper
import com.moban.model.data.Photo
import com.moban.model.data.homedeal.HomeDeal
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.data.project.Project
import com.moban.model.data.statistic.Statistic
import com.moban.model.data.user.User
import com.moban.model.response.PhotoResponse
import com.moban.model.response.SimpleResponse
import com.moban.model.response.post.PostResponse
import com.moban.model.response.post.PostStatusResponse
import com.moban.mvp.BaseMvpFragment
import com.moban.network.service.ImageService
import com.moban.network.service.PostService
import com.moban.util.*
import kotlinx.android.synthetic.main.fragment_new_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by lenvo on 4/26/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class NewHomeFragment : BaseMvpFragment<INewHomeFragmentView, INewHomeFragmentPresenter>(), INewHomeFragmentView {
    override var presenter: INewHomeFragmentPresenter = NewHomeFragmentPresenterIml()
    private var lowestId :Int? = null
    private var feedAdapter = NewFeedAdapter()
    private var noNetworkDialog: Dialog? = null
    private var dialog: Dialog? = null
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val postService = retrofit?.create(PostService::class.java)
    private val photoService = retrofit?.create(ImageService::class.java)

    private var loginRequiredDialog: Dialog? = null

    companion object {
        const val BROADCAST_SCROLL_REQUEST = "BROADCAST_SCROLL_REQUEST"
        const val BUNDLE_SCROLL_REQUEST_VALUE = "BUNDLE_SCROLL_REQUEST_VALUE"
    }

    private var scrollRequireReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        }
    }

    override fun onResume() {
        super.onResume()

        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context)
                .registerReceiver(scrollRequireReceiver, IntentFilter(BROADCAST_SCROLL_REQUEST))
    }

    override fun onPause() {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context).unregisterReceiver(scrollRequireReceiver)
        super.onPause()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_new_home
    }

    override fun initialize(savedInstanceState: Bundle?) {
        new_home_refresh_layout.isRefreshing = true
        initRecycleView()
        refreshData()
    }

    private fun stopLoading() {
        new_home_refresh_layout.isRefreshing = false
    }

    private fun requireLogin() {
        val context = activity!!

        loginRequiredDialog = DialogUtil.showLoginRequiredDialog(context, View.OnClickListener {
            SignInActivity.start(context, activity!!)
            loginRequiredDialog?.dismiss()
        })
    }

    private fun refreshData() {
        new_home_refresh_layout.isRefreshing = true

        presenter.getHighlightProjects()
        presenter.getRecentViewedProjects()
        presenter.banners()
        presenter.getHomeDeals()
        presenter.topPartners()

        lowestId = null
        presenter.posts(lowestId)
    }

    private fun initRecycleView() {
        new_home_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        new_home_refresh_layout.isRefreshing = true
        new_home_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        val linearLayoutManager = LinearLayoutManager(context)
        new_home_recycler_view.layoutManager = linearLayoutManager

        feedAdapter = initAdapter()

        feedAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.posts(lowestId)
            }
        }
        new_home_recycler_view.adapter = feedAdapter
    }

    private fun initAdapter(): NewFeedAdapter {
        feedAdapter.isShowHighlightProject = true

        val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false

        feedAdapter.listenerHeaderMenu = object: FeedItemHeaderItemHandler {
            override fun onBooked() {
                if (isAnonymous) {
                    activity?.let {
                        SignInActivity.start(context, it)
                    }
                }
                else {
                    SearchProjectActivity.start(context, SearchForType.RESERVATION)
                }
            }

            override fun onReward() {
                if (isAnonymous) {
                    activity?.let {
                        SignInActivity.start(context, it)
                    }
                }
                else {
                    LinkmartActivity.start(context, LinkmartMainCategory.linkReward.value)
                }
            }

            override fun onTransaction() {
                val user = LocalStorage.user() ?: return
                ListReservationActivity.start(context, user)
            }

            override fun onProjects() {
                (activity as? MainActivity)?.let {
                    it.showProjectTabBar()
                }
            }

            override fun onSecondaryProjects() {
                if (isAnonymous) {
                    activity?.let {
                        SignInActivity.start(context, it)
                    }
                }
                else {
                    SecondaryProjectsActivity.start(context)
                }
            }
        }

        feedAdapter.listener = object : FeedItemHandler {
            override fun onReact(post: Post) {
                if (isAnonymous) {
                    requireLogin()
                    return
                }

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
                if (isAnonymous) {
                    requireLogin()
                    return
                }

                if (post.is_Followed) {
                    unfollow(post)
                } else {
                    follow(post)
                }
            }

            override fun onShare(post: Post, bitmaps: List<Bitmap>) {
                Util.saveToClipboard(context, post.getFullContent())
                ShareHelper.sharePost(context, post)
            }

            override fun onExpandContent(post: Post) {
                post.isExpanded = true
                feedAdapter.replacePostItem(post)
            }

            override fun onNewPost(quickAction: NewPostQuickAction) {
                NewPostActivity.start(this@NewHomeFragment, context, quickAction)
            }

            override fun onViewProfileDetail(userId: Int) {
                AccountDetailActivity.start(context, userId)
            }

            override fun onDownloadPhoto(photo: com.moban.model.data.media.Photo) {
                downloadPhoto(photo)
            }

            override fun onClickURL(url: String) {
                WebViewActivity.start(context, getString(R.string.app_name), url)
            }

            override fun onDelete(post: Post) {
                deletePost(post)
            }

            override fun onEdit(post: Post) {
                EditPostActivity.start(this@NewHomeFragment, context, post)
            }

            override fun onReplyComment(comment: Comment) {

            }
        }

        feedAdapter.listenerWelcome = object : FeedWelcomeItemHandler {
            override fun onLogin() {
                (activity as? MainActivity)?.let {
                    SignInActivity.start(context, it)
                }
            }
        }

        return feedAdapter
    }

    private fun downloadPhoto(photo: com.moban.model.data.media.Photo) {
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                downloadPhoto(photo)
            })
            return
        }

        if (!Permission.checkPermissionWriteExternalStorage(context)) {
            return
        }

        photoService?.download(photo.photo_Link)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.let {
                    val fileName = FileUtil.getFileNameFromURL(photo.photo_Link)
                    if (BitmapUtil.saveImage(context, it.byteStream(), fileName)) {
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
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
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
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
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
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
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
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
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
        DialogUtil.showInfoDialog(context,
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
        PostDetailActivity.start(this, context, post)
    }

    private fun startCommentPost(post: Post) {
        PostDetailActivity.start(this, context, post, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LHApplication.instance.getFbManager()?.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.NEW_POST_REQUEST -> {
                data?.let { it ->
                    val newPost = it.getSerializableExtra(NewPostActivity.NEW_POST_KEY_DATA) as Post
                    feedAdapter.addFirstPost(newPost)
                    activity?.let {
                        val mainActivity = it as MainActivity
                        mainActivity.insertNewPostOnMyFeeds(newPost)
                    }
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
        if (!NetworkUtil.hasConnection(context)) {
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
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                deletePost(post)
            })
            return
        }

        dialog = DialogUtil.showConfirmDialog(context, true, getString(R.string.delete_post), getString(R.string.delete_post_comfirm),
                getString(R.string.delete), getString(R.string.skip),
                View.OnClickListener {
                    postService?.delete(post.id)?.enqueue(object : Callback<SimpleResponse> {
                        override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                            DialogUtil.showErrorDialog(context, getString(R.string.delete_post_error_title),
                                    getString(R.string.check_in_fail_desc), getString(R.string.ok), null)
                        }

                        override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                            response?.body()?.let {
                                dialog?.dismiss()
                                if (it.success) {
                                    feedAdapter?.removePost(post)
                                } else {
                                    DialogUtil.showErrorDialog(context, getString(R.string.delete_post_error_title),
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
        if (!AppUtil.hasFacebookApp(context)) {
            dialog = DialogUtil.showErrorDialog(context,
                    "Bạn chưa có Facebook app",
                    "Bạn hãy cài đặt Facebook app để chia sẻ bài viết nhé.",
                    getString(R.string.ok), View.OnClickListener {
                dialog?.dismiss()
                AppUtil.openAppPackage(activity!!, Constant.FACEBOOK_APP_PACKAGE_NAME)
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
        if (!NetworkUtil.hasConnection(context)) {
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

    override fun bindingPosts(feeds: List<Post>, lowestId: Int, canLoadMore: Boolean) {
        this.lowestId = lowestId
        if (feedAdapter.isDataEmpty) {
            feedAdapter.setPosts(feeds, canLoadMore)
        } else {
            feedAdapter.appendPosts(feeds, canLoadMore)
        }
        stopLoading()
    }

    override fun bindingBanner(photos: List<Photo>) {
        stopLoading()
        feedAdapter.bindingBanners(photos)
    }

    override fun bindingTopPartner(users: List<User>) {
        stopLoading()
        feedAdapter.bindTopPartner(users)
    }

    override fun bindingHomeDeals(deals: List<HomeDeal>) {
        stopLoading()
        val homeDeals = deals.filter { it.deals.isNotEmpty() }.filter { it.isValid() }
        feedAdapter.bindNewDealsProject(homeDeals)
    }

    override fun bindingHighlightProjects(project: List<Project>) {
        stopLoading()
        feedAdapter.bindingHighlightsProject(project)
    }

    override fun bindingStatistics(statistics: List<Statistic>) {
        stopLoading()
        feedAdapter.bindStatistics(statistics)
    }

    override fun bindingRecentViewedProjects(project: List<Project>) {
        stopLoading()
        feedAdapter.bindingRecentProject(project)
    }

}
