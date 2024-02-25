package com.moban.flow.newpost

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.feed.PostImageAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.enum.NewPostQuickAction
import com.moban.handler.PostImageItemHandler
import com.moban.handler.TopicItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.BitmapUpload
import com.moban.model.data.post.Post
import com.moban.model.data.post.Topic
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import com.moban.view.custom.CustomImageSelectorActivity
import com.yongchun.library.view.ImageSelectorActivity.*
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.nav_new_post.view.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class NewPostActivity : BaseMvpActivity<INewPostView, INewPostPresenter>(), INewPostView {
    override var presenter: INewPostPresenter = NewPostPresenterIml()

    private val postImageAdapter: PostImageAdapter = PostImageAdapter()
    private var currentNumImages = 0

    var quickAction: NewPostQuickAction = NewPostQuickAction.NONE
    var ratingId: Int = 0
    var projectId: Int = 0

    private var youtubeUrl: String = ""
    private var youtubeId: String = ""

    private var selectedTopic: Topic? = null
    private var topicDialog: Dialog? = null

    companion object {
        const val NEW_POST_KEY_DATA = "NEW_POST_KEY_DATA"
        const val NEW_POST_KEY_QUICK_ACTION = "NEW_POST_KEY_QUICK_ACTION"
        const val NEW_POST_KEY_RATING_ID = "NEW_POST_KEY_RATING_PARAM"
        const val NEW_POST_KEY_PROJECT_ID = "NEW_POST_KEY_PROJECT_ID"

        fun start(activity: Activity, quickAction: NewPostQuickAction) {
            val intent = Intent(activity, NewPostActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(NEW_POST_KEY_QUICK_ACTION, quickAction.value)
            intent.putExtras(bundle)

            activity.startActivityForResult(intent, Constant.NEW_POST_REQUEST)
        }

        fun start(homeFragment: Fragment, context: Context, quickAction: NewPostQuickAction) {
            val intent = Intent(context, NewPostActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(NEW_POST_KEY_QUICK_ACTION, quickAction.value)
            intent.putExtras(bundle)

            homeFragment.startActivityForResult(intent, Constant.NEW_POST_REQUEST)
        }

        fun start(activity: Activity) {
            val intent = Intent(activity, NewPostActivity::class.java)
            activity.startActivityForResult(intent, Constant.NEW_POST_REQUEST)
        }

        fun start(activity: Activity, ratingId: Int, projectId: Int) {
            val intent = Intent(activity, NewPostActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(NEW_POST_KEY_RATING_ID, ratingId)
            bundle.putInt(NEW_POST_KEY_PROJECT_ID, projectId)
            intent.putExtras(bundle)

            activity.startActivityForResult(intent, Constant.NEW_POST_REQUEST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_new_post
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(NEW_POST_KEY_QUICK_ACTION)) {
            quickAction = NewPostQuickAction.from(bundle?.getInt(NEW_POST_KEY_QUICK_ACTION) ?: 0)
        }

        if (intent.hasExtra(NEW_POST_KEY_RATING_ID)) {
            ratingId = bundle?.getInt(NEW_POST_KEY_RATING_ID) ?: 0
        }

        if (intent.hasExtra(NEW_POST_KEY_PROJECT_ID)) {
            projectId = bundle?.getInt(NEW_POST_KEY_PROJECT_ID) ?: 0
        }

        setGAScreenName("New Post", GACategory.FEED)

        fillSelectedTopic(null)
        fillYoutubeLink("")

        bindingBasicInfo()
        checkQuickAction()

        initRecycleView()

        new_post_add_image.setOnClickListener {
            KeyboardUtil.forceHideKeyboard(this)
            showViewInputYoutubeLink(false)
            showImagePicker()
        }

        new_post_add_youtube_link.setOnClickListener {
            val show = new_post_view_input_youtube.visibility
            showViewInputYoutubeLink(show == View.GONE)
        }

        new_post_btn_done_youtube.setOnClickListener {
            val text = new_post_edt_link_youtube.text.toString().trim()
            fillYoutubeLink(text)

            if (youtubeId.isNotEmpty() || text.isEmpty()) {
                KeyboardUtil.forceHideKeyboard(this)
                showViewInputYoutubeLink(false)
            } else {
                DialogUtil.showErrorDialog(
                    this, "",
                    "Link Youtube không hợp lệ", getString(R.string.ok), null
                )
            }
        }

        new_post_add_topic.setOnClickListener {
            KeyboardUtil.forceHideKeyboard(this)
            showViewInputYoutubeLink(false)
            showListTopics()
        }

        new_post_selected_topic_delete.setOnClickListener {
            fillSelectedTopic(null)
        }

        new_post_selected_youtube_link_delete.setOnClickListener {
            fillYoutubeLink("")
        }

        if (ratingId > 0) {
            new_post_nav.nav_new_tv_title.text = getString(R.string.review)
        }

        new_post_nav.nav_tvCancel.setOnClickListener {
            finish()
        }

        new_post_nav.nav_tvOK.setOnClickListener {
            submitPost()
        }
    }

    private fun checkQuickAction() {
        when (quickAction) {
            NewPostQuickAction.PHOTO -> showImagePicker()
            NewPostQuickAction.YOUTUBE_LINK -> showViewInputYoutubeLink(true)
            NewPostQuickAction.TOPIC -> showListTopics()

            else -> {

            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)

        new_post_recycler_view.layoutManager = layoutManager
        new_post_recycler_view.isNestedScrollingEnabled = false
        new_post_recycler_view.adapter = postImageAdapter

        postImageAdapter.listener = object : PostImageItemHandler {
            override fun onRemove(bitmap: BitmapUpload) {
                presenter.removeImage(bitmap)
                postImageAdapter.removeImage(bitmap)
                currentNumImages--
            }
        }
    }

    private fun showListTopics() {
        val topics = LHApplication.instance.lhCache.postTopics

        topicDialog = DialogBottomSheetUtil.showDialogListTopics(
            this,
            selectedTopic,
            topics,
            object : TopicItemHandler {
                override fun onClicked(topic: Topic) {
                    fillSelectedTopic(topic)
                    topicDialog?.dismiss()
                }
            })
    }

    private fun showViewInputYoutubeLink(show: Boolean) {
        new_post_view_input_youtube.visibility = if (show) View.VISIBLE else View.GONE

        if (show) {
            new_post_edt_link_youtube.requestFocus()
            KeyboardUtil.showKeyboard(this)
        } else {
            KeyboardUtil.forceHideKeyboard(this)
        }

        val colorId = if (show) R.color.colorAccent else R.color.color_black_60
        val color = Util.getColor(this, colorId)
        new_post_add_youtube_link_image.setColorFilter(color)
        new_post_add_youtube_link_title.setTextColor(color)
    }

    private fun bindingBasicInfo() {
        LocalStorage.user()?.let {
            new_post_tv_full_name.text = it.name

            val tagName = "@" + it.username
            new_post_tv_user_name.text = tagName

            LHPicasso.loadImage(it.avatar, new_post_img_avatar)
        }
    }

    private fun fillSelectedTopic(topic: Topic?) {
        selectedTopic = topic
        new_post_selected_topic_name.text = topic?.topic ?: ""
        new_post_selected_topic.visibility = if (topic == null) View.GONE else View.VISIBLE
    }

    private fun fillYoutubeLink(link: String) {
        youtubeUrl = link
        youtubeId = getYoutubeIdFromLink(link)

        new_post_selected_youtube_link_content.text = youtubeUrl
        new_post_selected_youtube_link.visibility =
            if (youtubeId.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun getYoutubeIdFromLink(link: String): String {
        var videoId: String = ""
        val regex =
            "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)"
        val pattern: Pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(link)
        if (matcher.find()) {
            videoId = matcher.group(1)
        }
        return videoId
    }

    private fun showImagePicker() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(this, CustomImageSelectorActivity::class.java)
        intent.putExtra(EXTRA_MAX_SELECT_NUM, Constant.MAX_IMAGE_UPLOAD - currentNumImages)
        intent.putExtra(EXTRA_SELECT_MODE, 1)
        intent.putExtra(EXTRA_SHOW_CAMERA, true)
        intent.putExtra(EXTRA_ENABLE_PREVIEW, false)
        intent.putExtra(EXTRA_ENABLE_CROP, false)

        if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionCamera(
                this
            )
        ) {
            startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    /**
     * On result callback
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_IMAGE
        ) {
            data?.let {
                val images = it.getSerializableExtra(REQUEST_OUTPUT)
                        as ArrayList<String>

                val bitmaps: MutableList<BitmapUpload> = ArrayList()
                images.mapTo(bitmaps) {
                    BitmapUpload(Date().time, BitmapUtil.getBitmapFromFile(it))
                }
                currentNumImages += bitmaps.size

                postImageAdapter.images.addAll(bitmaps)
                postImageAdapter.notifyDataSetChanged()
                presenter.uploadImages(ArrayList(bitmaps))
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

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

    private fun submitPost() {
        val content = new_post_et_content.text

        if (content.isEmpty() && postImageAdapter.images.isEmpty() && youtubeId.isEmpty()) {
            if (ratingId > 0) {
                var dialog: Dialog? = null
                dialog = DialogUtil.showConfirmDialog(this,
                    false,
                    "",
                    "Không có nội dung để gửi đánh giá cho dự án. Bạn có muốn bỏ qua bước nhận xét này không?",
                    getString(R.string.action_continue),
                    getString(R.string.skip),
                    View.OnClickListener {
                        dialog?.dismiss()
                    },
                    View.OnClickListener {
                        dialog?.dismiss()
                        finish()
                    })
            } else {
                DialogUtil.showErrorDialog(
                    this,
                    getString(R.string.new_post_error_title),
                    getString(R.string.new_post_error_empty_input),
                    getString(R.string.ok), null
                )
            }
            return
        }

        new_post_progress.visibility = View.VISIBLE
        presenter.createPost(
            content.toString(),
            youtubeId,
            selectedTopic?.id ?: 0,
            ArrayList(postImageAdapter.images),
            ratingId,
            projectId
        )
    }

    override fun submitPostCompleted(post: Post) {
        val intent = Intent(this, NewPostActivity::class.java)
        intent.putExtra(NEW_POST_KEY_DATA, post)

        setResult(Constant.NEW_POST_REQUEST, intent)

        new_post_progress.visibility = View.GONE

        finish()
    }

    override fun submitPostFailed() {
        DialogUtil.showErrorDialog(
            this,
            getString(R.string.new_post_error_title),
            getString(R.string.new_post_error_desc),
            getString(R.string.ok), null
        )
        new_post_progress.visibility = View.GONE
    }
}
