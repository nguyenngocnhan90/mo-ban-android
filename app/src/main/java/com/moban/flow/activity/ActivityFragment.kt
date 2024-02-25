package com.moban.flow.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import com.moban.adapter.feed.FeedAdapter
import com.moban.adapter.user.HeaderAdapter
import com.moban.constant.Constant
import com.moban.constant.ServerURL
import com.moban.enum.LinkmartMainCategory
import com.moban.enum.NewPostQuickAction
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.flow.editpost.EditPostActivity
import com.moban.flow.linkmart.LinkmartActivity
import com.moban.flow.main.MainActivity
import com.moban.flow.newpost.NewPostActivity
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.secondary.project.SecondaryProjectsActivity
import com.moban.flow.signin.SignInActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.FeedItemHandler
import com.moban.handler.FeedProjectTypeItemHandler
import com.moban.handler.LoadMoreHandler
import com.moban.handler.RecyclerItemListener
import com.moban.helper.LocalStorage
import com.moban.helper.ShareHelper
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.data.post.Topic
import com.moban.model.data.project.Project
import com.moban.model.response.PhotoResponse
import com.moban.model.response.SimpleResponse
import com.moban.model.response.post.ListPostResponse
import com.moban.model.response.post.ListTopicResponse
import com.moban.model.response.post.PostResponse
import com.moban.model.response.post.PostStatusResponse
import com.moban.model.response.project.ListProjectResponse
import com.moban.network.service.ImageService
import com.moban.network.service.PostService
import com.moban.network.service.ProjectService
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_list_reservation.*
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityFragment : Fragment() {

    companion object {
        const val BROADCAST_SCROLL_REQUEST = "BROADCAST_SCROLL_REQUEST"
        const val BUNDLE_SCROLL_REQUEST_VALUE = "BUNDLE_SCROLL_REQUEST_VALUE"
    }

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val postService = retrofit?.create(PostService::class.java)
    private val photoService = retrofit?.create(ImageService::class.java)
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)

    private var lowestId = 0
    private var feedAdapter: FeedAdapter? = null

    private var topicAdapter: HeaderAdapter = HeaderAdapter()
    private var selectedTopic: Topic? = null

    private var noNetworkDialog: Dialog? = null
    private var dialog: Dialog? = null
    private val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
    private var loginRequiredDialog: Dialog? = null

    private lateinit var fragmentActivity: FragmentActivity

    private var scrollRequireReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = this.activity!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()

        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(fragmentActivity)
                .registerReceiver(scrollRequireReceiver, IntentFilter(BROADCAST_SCROLL_REQUEST))
    }

    override fun onPause() {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(fragmentActivity).unregisterReceiver(scrollRequireReceiver)
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initRecyclerTopics()

        getFeeds(true)
        loadHighlightProject()
    }

    private fun requireLogin() {
        val context = activity!!

        loginRequiredDialog = DialogUtil.showLoginRequiredDialog(context, View.OnClickListener {
            SignInActivity.start(context, activity!!)
            loginRequiredDialog?.dismiss()
        })
    }

    private fun initRecyclerView() {
        home_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        home_refresh_layout.isRefreshing = true
        home_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        val linearLayoutManager = LinearLayoutManager(fragmentActivity)
        home_recycler_view.layoutManager = linearLayoutManager

        feedAdapter = initAdapter()

        feedAdapter?.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                getFeeds(false)
            }
        }
        home_recycler_view.adapter = feedAdapter
    }

    private fun initRecyclerTopics() {
        val layoutManager = LinearLayoutManager(fragmentActivity, LinearLayoutManager.HORIZONTAL, false)
        home_recycler_view_topics.layoutManager = layoutManager
        home_recycler_view_topics.adapter = topicAdapter

        val topics = LHApplication.instance.lhCache.postFilterTopics
        if (topics.isEmpty()) {
            loadPostFilterTopics()
        }
        showListTopics()
    }

    private fun showListTopics() {
        val topicAll = Topic()
        topicAll.topic = getString(R.string.all)
        val topics = LHApplication.instance.lhCache.postFilterTopics.toMutableList()
        topics.add(0, topicAll)

        topicAdapter.texts = topics.map { it.topic }

        topicAdapter.recyclerItemListener = object : RecyclerItemListener {
            override fun onClick(view: View, index: Int) {
                if (index < topics.count() && topicAdapter.currentIndex != index) {
                    topicAdapter.currentIndex = index
                    topicAdapter.notifyDataSetChanged()

                    feedAdapter?.setPosts(ArrayList(), false)
                    refreshData()

                    selectedTopic = topics[index]
                    home_refresh_layout?.isRefreshing = true
                    refreshData()
                }
            }
        }
    }

    private fun initAdapter(): FeedAdapter {
        val adapter = FeedAdapter()

        LocalStorage.user()?.let {
            adapter.user = it
            adapter.isCanPost = it.can_Post
        }

        adapter.listenerSecondary = object: FeedProjectTypeItemHandler {
            override fun onSecondary() {
                SecondaryProjectsActivity.start(fragmentActivity)
            }

            override fun onEvent() {
                WebViewActivity.start(fragmentActivity, getString(R.string.event), ServerURL.eventURL, oneTimeToken = true)
            }

            override fun onService() {
                val isBO = LocalStorage.user()?.isBO ?: false
                if (isBO) {
                    WebViewActivity.start(fragmentActivity, getString(R.string.okr), ServerURL.orkURL, oneTimeToken = true)
                } else {
                    WebViewActivity.start(fragmentActivity, getString(R.string.service_title), ServerURL.serviceURL, oneTimeToken = true)
                }
            }

            override fun onInsurance() {
                WebViewActivity.start(fragmentActivity, getString(R.string.insurance), ServerURL.insuranceURL, oneTimeToken = true)
            }

            override fun onLinkReward() {
                LocalStorage.user()?.let {
                    if (!it.isOutsideAgent) {
                        LinkmartActivity.start(fragmentActivity, LinkmartMainCategory.linkReward.value)
                    }
                }
            }

            override fun onLinkHub() {
                LocalStorage.user()?.let {
                    if (!it.isOutsideAgent) {
                        LinkmartActivity.start(fragmentActivity, LinkmartMainCategory.linkHub.value)
                    }
                }
            }

            override fun onHurom() {
                ProjectDetailActivity.start(fragmentActivity, Constant.HUROM_PROJECT_ID)
            }

            override fun onSmartHome() {
                ProjectDetailActivity.start(fragmentActivity, Constant.SMARTHOME_PROJECT_ID)
            }
        }

        adapter.listener = object : FeedItemHandler {
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
                Util.saveToClipboard(fragmentActivity, post.getFullContent())
                ShareHelper.sharePost(fragmentActivity, post)
            }

            override fun onExpandContent(post: Post) {
                post.isExpanded = true
                adapter.replacePostItem(post)
            }

            override fun onNewPost(quickAction: NewPostQuickAction) {
                NewPostActivity.start(this@ActivityFragment, fragmentActivity, quickAction)
            }

            override fun onViewProfileDetail(userId: Int) {
                AccountDetailActivity.start(fragmentActivity, userId)
            }

            override fun onDownloadPhoto(photo: Photo) {
                downloadPhoto(photo)
            }

            override fun onClickURL(url: String) {
                WebViewActivity.start(fragmentActivity, getString(R.string.app_name), url)
            }

            override fun onDelete(post: Post) {
                deletePost(post)
            }

            override fun onEdit(post: Post) {
                EditPostActivity.start(this@ActivityFragment, fragmentActivity, post)
            }

            override fun onReplyComment(comment: Comment) {

            }
        }

        return adapter
    }

    private fun downloadPhoto(photo: Photo) {

        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
                dismissNetworkDialog()
                downloadPhoto(photo)
            })
            return
        }

        if (!Permission.checkPermissionWriteExternalStorage(fragmentActivity)) {
            return
        }

        photoService?.download(photo.photo_Link)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.let {
                    val fileName = FileUtil.getFileNameFromURL(photo.photo_Link)
                    if (BitmapUtil.saveImage(fragmentActivity, it.byteStream(), fileName)) {
                        sendTracking(photo, Constant.NUM_RETRY_FAILING)
                    }
                }
            }
        })
    }

    private fun sharePost(post: Post, bitmaps: List<Bitmap>) {
        if (!AppUtil.hasFacebookApp(fragmentActivity)) {
            dialog = DialogUtil.showErrorDialog(fragmentActivity,
                    "Bạn chưa có Facebook app",
                    "Bạn hãy cài đặt Facebook app để chia sẻ bài viết nhé.",
                    getString(R.string.ok), View.OnClickListener {
                dialog?.dismiss()
                AppUtil.openAppPackage(fragmentActivity, Constant.FACEBOOK_APP_PACKAGE_NAME)
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
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
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

    /**
     * Call API
     */

    private fun loadPostFilterTopics() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        postService?.filterTopics()?.enqueue(object : Callback<ListTopicResponse> {
            override fun onFailure(call: Call<ListTopicResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ListTopicResponse>,
                response: Response<ListTopicResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        LHApplication.instance.lhCache.postFilterTopics = it.list
                        showListTopics()
                    }
                }
            }
        })
    }

    private fun refreshData() {
        lowestId = 0
        getFeeds(true)
    }

    private fun getFeeds(isRefreshing: Boolean) {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
                dismissNetworkDialog()
                getFeeds(isRefreshing)
            })
            return
        }

        val currentId = if (lowestId > 0 && !isRefreshing) lowestId else null
        val topicId = selectedTopic?.id ?: 0

        postService?.posts(currentId, topicId)?.enqueue(object : Callback<ListPostResponse> {
            override fun onFailure(call: Call<ListPostResponse>?, t: Throwable?) {
                home_refresh_layout?.isRefreshing = false
            }

            override fun onResponse(call: Call<ListPostResponse>?, response: Response<ListPostResponse>?) {
                home_refresh_layout?.isRefreshing = false

                response?.body()?.data?.let { data ->
                    val feeds = data.list

                    if (feeds.isNotEmpty()) {
                        lowestId = feeds.last().id
                        val canLoadMore = if (data.paging != null) {
                            data.paging!!.paging_can_load_more
                        } else false

                        if (isRefreshing || feedAdapter?.isDataEmpty == true) {
                            feedAdapter?.setPosts(feeds, canLoadMore)
                        } else {
                            feedAdapter?.appendPosts(feeds, canLoadMore)
                        }
                    }
                }
            }
        })
    }

    /**
     * Like / unlike
     */

    private fun like(post: Post) {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
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

                        feedAdapter?.reloadPost(post)
                    }
                }
            }
        })
    }

    private fun unlike(post: Post) {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
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

                        feedAdapter?.reloadPost(post)
                    }
                }
            }
        })
    }

    /**
     * Follow
     */

    private fun follow(post: Post) {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
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
                    feedAdapter?.reloadPost(it)

                    showMessagedFollowed()
                }
            }
        })
    }

    private fun unfollow(post: Post) {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
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
                    feedAdapter?.reloadPost(it)
                }
            }
        })
    }

    private fun showMessagedFollowed() {
        DialogUtil.showInfoDialog(fragmentActivity,
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
        PostDetailActivity.start(this, fragmentActivity, post)
    }

    private fun startCommentPost(post: Post) {
        PostDetailActivity.start(this, fragmentActivity, post, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LHApplication.instance.getFbManager()?.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.NEW_POST_REQUEST -> {
                data?.let { it ->
                    val newPost = it.getSerializableExtra(NewPostActivity.NEW_POST_KEY_DATA) as Post
                    feedAdapter?.addFirstPost(newPost)
                    activity?.let {
                        val mainActivity = it as MainActivity
                        mainActivity.insertNewPostOnMyFeeds(newPost)
                    }
                }
            }
            Constant.EDIT_POST_REQUEST -> {
                data?.let {
                    val updatePost = it.getSerializableExtra(EditPostActivity.EDIT_POST_KEY_DATA) as Post
                    feedAdapter?.reloadPost(updatePost)
                }
            }
            Constant.DETAIL_POST_REQUEST -> {
                data?.let {
                    val updatePost = it.getSerializableExtra(PostDetailActivity.BUNDLE_KEY_POST) as Post
                    feedAdapter?.reloadPost(updatePost)
                }
            }
        }
    }

    private fun sendTracking(photo: Photo, numOfRetry: Int) {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
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
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            noNetworkDialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
                dismissNetworkDialog()
                deletePost(post)
            })
            return
        }

        dialog = DialogUtil.showConfirmDialog(fragmentActivity, true, getString(R.string.delete_post), getString(R.string.delete_post_comfirm),
                getString(R.string.delete), getString(R.string.skip),
                View.OnClickListener {
                    postService?.delete(post.id)?.enqueue(object : Callback<SimpleResponse> {
                        override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                            DialogUtil.showErrorDialog(fragmentActivity, getString(R.string.delete_post_error_title),
                                    getString(R.string.check_in_fail_desc), getString(R.string.ok), null)
                        }

                        override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                            response?.body()?.let {
                                dismissDialog()
                                if (it.success) {
                                    feedAdapter?.removePost(post)
                                } else {
                                    DialogUtil.showErrorDialog(fragmentActivity, getString(R.string.delete_post_error_title),
                                            getString(R.string.check_in_fail_desc), getString(R.string.ok), null)
                                }
                            }
                        }
                    })
                }, View.OnClickListener {
                    dismissDialog()
                })
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    fun insertNewPost(newPost: Post) {
        feedAdapter?.addFirstPost(newPost)
    }

    fun loadHighlightProject() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.highlight()?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {

                val data = response?.body()?.data
                if (data != null) {
                    bindingHighlightsProject(data.list)
                }
            }
        })
    }

    private fun bindingHighlightsProject(list: List<Project>) {
        LHApplication.instance.lhCache.highlightProject = list
        feedAdapter?.bindingHighlightsProject()
    }

}
