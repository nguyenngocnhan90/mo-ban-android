package com.moban.flow.postdetail

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.moban.adapter.feed.FeedCommentAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.enum.NewPostQuickAction
import com.moban.flow.account.detail.AccountDetailActivity
import com.moban.flow.editpost.EditPostActivity
import com.moban.flow.signin.SignInActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.flow.youtube.YoutubeVideoFragment
import com.moban.handler.FeedItemHandler
import com.moban.handler.IYoutubeContainer
import com.moban.handler.LoadMoreHandler
import com.moban.helper.LocalStorage
import com.moban.helper.ShareHelper
import com.moban.model.data.BitmapUpload
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.response.post.NewPostData
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import com.moban.view.custom.CustomImageSelectorActivity
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.nav.view.*
import java.util.*
import kotlin.collections.ArrayList

class PostDetailActivity : BaseMvpActivity<IPostDetailView, IPostDetailPresenter>(),
    IPostDetailView, IYoutubeContainer {

    companion object {
        const val BUNDLE_KEY_POST = "BUNDLE_KEY_POST"
        const val BUNDLE_KEY_POST_ID = "BUNDLE_KEY_POST_ID"
        const val BUNDLE_KEY_POST_IS_RATING = "BUNDLE_KEY_POST_IS_RATRING"
        const val BUNDLE_KEY_COMMENT_FORWARD = "BUNDLE_KEY_COMMENT_FORWARD"

        fun start(fragment: Fragment, context: Context, post: Post) {
            start(fragment, context, post, false)
        }

        fun start(fragment: Fragment, context: Context, post: Post, isCommentForward: Boolean) {
            val intent = Intent(context, PostDetailActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_POST, post)
            bundle.putBoolean(BUNDLE_KEY_COMMENT_FORWARD, isCommentForward)

            intent.putExtras(bundle)
            fragment.startActivityForResult(intent, Constant.DETAIL_POST_REQUEST)
        }

        fun start(activity: Activity, context: Context, post: Post, isCommentForward: Boolean) {
            val intent = Intent(context, PostDetailActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_POST, post)
            bundle.putBoolean(BUNDLE_KEY_COMMENT_FORWARD, isCommentForward)

            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.DETAIL_POST_REQUEST)
        }

        fun start(context: Context, postId: Int, isRatingPost: Boolean = false) {
            val intent = Intent(context, PostDetailActivity::class.java)

            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY_POST_ID, postId)
            bundle.putBoolean(BUNDLE_KEY_POST_IS_RATING, isRatingPost)

            intent.putExtras(bundle)
            context.startActivity(intent)
        }

    }

    override var presenter: IPostDetailPresenter = PostDetailPresenterIml()

    private var post: Post? = null
    private var postId: Int = 0
    private var isReload: Boolean = false
    private var isRatingPost: Boolean = false
    private var adapter: FeedCommentAdapter = FeedCommentAdapter()
    private var dialog: Dialog? = null

    private var replyingComment: Comment? = null
    private var attachedBitmap: BitmapUpload? = null

    private val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
    private var loginRequiredDialog: Dialog? = null

    // Youtube view
    private var flVideoContainer: FrameLayout? = null
    private var youtubeVideoFragment: YoutubeVideoFragment? = null
    private var viewYoutubePos = -1

    override fun getLayoutId(): Int {
        return R.layout.activity_post_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras

        if (intent.hasExtra(BUNDLE_KEY_POST)) {
            post = bundle?.getSerializable(BUNDLE_KEY_POST) as Post

            post?.let {
                postId = it.id

                it.user_Profile_Name?.let { name ->
                    val titleFirst = if (isRatingPost) "Đánh Giá của" else "Bài Viết của"
                    val title = "$titleFirst $name"
                    post_detail_nav.nav_tvTitle.text = title
                }
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_COMMENT_FORWARD)) {
            val showKeyboard = bundle?.getBoolean(BUNDLE_KEY_COMMENT_FORWARD) ?: false
            if (showKeyboard) {
                Handler().postDelayed({
                    showKeyboardToComment()
                }, 500)
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_POST_ID)) {
            postId = bundle?.getInt(BUNDLE_KEY_POST_ID) ?: 0
        }

        if (intent.hasExtra(BUNDLE_KEY_POST_IS_RATING)) {
            isRatingPost = bundle?.getBoolean(BUNDLE_KEY_POST_IS_RATING) ?: false
        }

        post_detail_nav.nav_imgBack.setOnClickListener {
            returnHomePage()
        }

        initRecyclerView()
        initTextInput()
        loadMyAccount()

        clearReplyingComment()
        deleteAttachedImage()

        // Get comments
        if (post != null) {
            presenter.getComments(postId)
        } else if (postId != 0) {
            post_detail_refresh_layout.isRefreshing = true
            presenter.getPost(postId)
        }

        setGAScreenName("Notification Detail $postId", GACategory.FEED)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        returnHomePage()
    }

    private fun returnHomePage() {
        if (isReload) {
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra(BUNDLE_KEY_POST, post)

            setResult(Constant.DETAIL_POST_REQUEST, intent)
        }
        finish()
    }

    private fun showKeyboardToComment() {
        if (isAnonymous) {
            requireLogin()
        } else {
            post_detail_edt_comment.requestFocus()
            KeyboardUtil.showKeyboard(this)
        }
    }

    private fun loadMyAccount() {
        LocalStorage.user()?.let {
            LHPicasso.loadImage(it.avatar, post_detail_my_avatar_img)
        }
    }

    private fun requireLogin() {
        val context = this

        loginRequiredDialog = DialogUtil.showLoginRequiredDialog(context, View.OnClickListener {
            SignInActivity.start(context, context)
            loginRequiredDialog?.dismiss()
        })
    }

    private fun initTextInput() {
        post_detail_edt_comment.isFocusable = !isAnonymous

        post_detail_edt_comment.setOnClickListener {
            if (isAnonymous) {
                requireLogin()
            }
        }

        post_detail_edt_comment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val content = charSequence.toString()
                val enabled = content.isNotEmpty() || attachedBitmap != null
                setButtonSendEnabled(enabled)
            }
        })

        post_detail_btn_send.setOnClickListener {
            post?.let {
                val content = post_detail_edt_comment.text.toString()
                if (content.isNotEmpty() || attachedBitmap != null) {
                    KeyboardUtil.forceHideKeyboard(this)
                    presenter.postComment(it, replyingComment, content, attachedBitmap)
                }
            }
        }

        post_detail_btn_pick_image.setOnClickListener {
            showImagePicker()
        }

        post_detail_btn_delete_attached_image.setOnClickListener {
            deleteAttachedImage()
        }

        post_detail_btn_close_replying.setOnClickListener {
            clearReplyingComment()
        }
    }

    private fun setButtonSendEnabled(enabled: Boolean) {
        if (enabled) {
            enableButtonSend()
        }
        else {
            disableButtonSend()
        }
    }

    private fun enableButtonSend() {
        post_detail_btn_send.isEnabled = true

        post_detail_btn_send.setTextColor(
            ContextCompat.getColor(
                this@PostDetailActivity,
                R.color.colorAccent
            )
        )
    }

    private fun disableButtonSend() {
        post_detail_btn_send.isEnabled = false

        post_detail_btn_send.setTextColor(
            ContextCompat.getColor(
                this@PostDetailActivity,
                R.color.color_light_gray
            )
        )
    }

    private fun initRecyclerView() {
        post_detail_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        post_detail_refresh_layout.setOnRefreshListener {
            presenter.refreshComments(postId)
        }

        val linearLayoutManager = LinearLayoutManager(this)
        post_detail_recycler_view.layoutManager = linearLayoutManager

        adapter.post = post
        post_detail_recycler_view.adapter = adapter

        val context = this

        adapter.listener = object : FeedItemHandler {
            override fun onReact(post: Post) {
                if (isAnonymous) {
                    requireLogin()
                    return
                }

                if (post.is_Liked) {
                    presenter.unlike(post)
                } else {
                    presenter.like(post)
                }
            }

            override fun onComment(post: Post) {
                showKeyboardToComment()
            }

            override fun onViewFeed(post: Post) {
            }

            override fun onFollow(post: Post) {
                if (isAnonymous) {
                    requireLogin()
                    return
                }

                if (post.is_Followed) {
                    presenter.unfollow(postId)
                } else {
                    presenter.follow(postId)
                }
            }

            override fun onShare(post: Post, bitmaps: List<Bitmap>) {
                Util.saveToClipboard(context, post.getFullContent())
                ShareHelper.sharePost(context, post)
            }

            override fun onExpandContent(post: Post) {
            }

            override fun onNewPost(quickAction: NewPostQuickAction) {

            }

            override fun onViewProfileDetail(userId: Int) {
                AccountDetailActivity.start(this@PostDetailActivity, userId)
            }

            override fun onDownloadPhoto(photo: Photo) {
                presenter.downloadPhoto(photo)
            }

            override fun onClickURL(url: String) {
                WebViewActivity.start(this@PostDetailActivity, getString(R.string.app_name), url)
            }

            override fun onDelete(post: Post) {

            }

            override fun onEdit(post: Post) {
                EditPostActivity.start(this@PostDetailActivity, context, post)
            }

            override fun onReplyComment(comment: Comment) {
                replyingComment = comment
                showReplyingCommentInfo()

                showKeyboardToComment()
            }
        }

        adapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.getComments(postId)
            }
        }
    }

    private fun sharePost(post: Post, bitmaps: List<Bitmap>) {
        val context = this

        if (!AppUtil.hasFacebookApp(context)) {
            dialog = DialogUtil.showErrorDialog(context,
                "Bạn chưa có Facebook app",
                "Bạn hãy cài đặt Facebook app để chia sẻ bài viết nhé.",
                getString(R.string.ok),
                View.OnClickListener {
                    dialog?.dismiss()
                    AppUtil.openAppPackage(context, Constant.FACEBOOK_APP_PACKAGE_NAME)
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
        shareDialog.registerCallback(
            LHApplication.instance.getFbManager(),
            object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result?) {
                    trackShare(post)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                }
            })
    }

    private fun showImagePicker() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(this, CustomImageSelectorActivity::class.java)
        intent.putExtra(ImageSelectorActivity.EXTRA_MAX_SELECT_NUM, 1)
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_MODE, 1)
        intent.putExtra(ImageSelectorActivity.EXTRA_SHOW_CAMERA, true)
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_PREVIEW, false)
        intent.putExtra(ImageSelectorActivity.EXTRA_ENABLE_CROP, false)

        if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionCamera(
                this
            )
        ) {
            startActivityForResult(intent, ImageSelectorActivity.REQUEST_IMAGE)
        }
    }

    private fun loadAttachedImage() {
        val bitmap = attachedBitmap?.bitmap
        post_detail_view_attached_image.visibility = if (bitmap == null) View.GONE else View.VISIBLE
        bitmap?.let {
            post_detail_attached_image.setImageBitmap(it)

            val width = it.width
            val height = it.height
            if (height > 0) {
                val param = post_detail_view_attached_image.layoutParams
                param.width = width * param.height / height
                post_detail_view_attached_image.layoutParams = param
            }
        }

        val enabled = post_detail_edt_comment.text.isNotEmpty() || attachedBitmap != null
        setButtonSendEnabled(enabled)
    }

    private fun deleteAttachedImage() {
        attachedBitmap = null
        loadAttachedImage()
    }

    private fun showReplyingCommentInfo() {
        post_detail_view_replying.visibility =
            if (replyingComment == null) View.GONE else View.VISIBLE
        post_detail_tv_replying_user.text = replyingComment?.user_Profile_Name ?: ""
    }

    private fun clearReplyingComment() {
        replyingComment = null
        showReplyingCommentInfo()
    }

    /**
     * On result callback
     */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constant.READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImagePicker()
                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LHApplication.instance.getFbManager()?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.EDIT_POST_REQUEST) {
            data?.let {
                val updatePost =
                    it.getSerializableExtra(EditPostActivity.EDIT_POST_KEY_DATA) as Post
                updatePost(updatePost)
            }
        }

        if (resultCode == Activity.RESULT_OK &&
            requestCode == ImageSelectorActivity.REQUEST_IMAGE
        ) {
            data?.let {
                val images = it.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT)
                        as ArrayList<String>

                val bitmaps: MutableList<BitmapUpload> = ArrayList()
                images.mapTo(bitmaps) { file ->
                    BitmapUpload(Date().time, BitmapUtil.getBitmapFromFile(file))
                }

                attachedBitmap = bitmaps.first()
                loadAttachedImage()
            }
        }
    }

    private fun updatePost(post: Post) {
        this.post = post
        adapter.post = post
        isReload = true
    }

    private fun trackShare(post: Post) {
        presenter.trackShare(post.id, Constant.NUM_RETRY_FAILING)
    }

    /**
     * View handlers
     */

    override fun reloadPost(post: Post) {
        adapter.post = post
        isReload = true
        post.user_Profile_Name?.let {
            val titleFirst = if (isRatingPost) "Đánh Giá của" else "Bài Viết của"
            val title = "$titleFirst $it"
            post_detail_nav.nav_tvTitle.text = title
        }
    }

    override fun setComments(comments: List<Comment>) {
        post_detail_refresh_layout.isRefreshing = false

        adapter.comments = comments
        adapter.notifyDataSetChanged()
    }

    override fun appendComments(comments: List<Comment>) {
        post_detail_refresh_layout.isRefreshing = false
        adapter.appendComments(comments)
    }

    override fun addMyComment(result: NewPostData) {
        post_detail_edt_comment.setText("")

        // Reload post info
        result.post_info?.let {
            adapter.post?.let { post ->
                post.comments = it.comments
                post.likes = it.likes

                reloadPost(post)
            }
        }

        // Append my comment to the bottom
        result.new_comment?.let {
            replyingComment?.let { replyingComment ->
                val subComments = replyingComment.sub_Comments?.toMutableList() ?: ArrayList()
                subComments.add(it)
                replyingComment.sub_Comments = subComments

                adapter.reloadSubComments(replyingComment)
            } ?: run {
                adapter.appendMyComment(it)
            }
        }

        clearReplyingComment()
        deleteAttachedImage()
        disableButtonSend()
    }

    override fun didGetPost(post: Post) {
        post_detail_refresh_layout.isRefreshing = false

        this.post = post
        presenter.getComments(postId)
    }

    override fun showFollowedMessage() {
        DialogUtil.showInfoDialog(
            this,
            getString(R.string.feed_you_did_follow),
            getString(R.string.feed_followed_msg),
            getString(R.string.ok), null
        )
    }

    /**
     * Youtube container
     */

    override fun sendLoggingEvent(action: String) {

    }

    // YouTube video
    fun initVideoFrameLayout(youtubeVideoFragment: YoutubeVideoFragment) {
        fragmentManager.beginTransaction()
            .replace(
                getFlVideoContainer().id, youtubeVideoFragment,
                YoutubeVideoFragment.TAG
            )
            .commitAllowingStateLoss()
    }

    override fun getFlVideoContainer(): FrameLayout {
        if (flVideoContainer == null) {
            flVideoContainer = FrameLayout(this)

            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
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
        val y = playerLocation[1]
        val pagerLocation = IntArray(2)
        post_detail_recycler_view.getLocationOnScreen(pagerLocation)
        val relativeY = y - pagerLocation[1]
        val topBarHeight = 45

        if (relativeY < topBarHeight) {
            post_detail_recycler_view.smoothScrollBy(0, relativeY - topBarHeight)
        } else if (relativeY + getFlVideoContainer().height > post_detail_recycler_view.height) {
            val value = relativeY + getFlVideoContainer().height - post_detail_recycler_view.height
            post_detail_recycler_view.smoothScrollBy(0, value)
        }
    }

    override fun releaseYouTubePlayer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
