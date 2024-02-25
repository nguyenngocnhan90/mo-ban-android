package com.moban.flow.editpost

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.moban.flow.newpost.NewPostActivity
import com.moban.handler.PostImageItemHandler
import com.moban.handler.TopicItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.BitmapUpload
import com.moban.model.data.post.Post
import com.moban.model.data.post.Topic
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import com.moban.view.custom.CustomImageSelectorActivity
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.nav_new_post.view.*
import java.util.*
import kotlin.collections.ArrayList

class EditPostActivity : BaseMvpActivity<IEditPostView, IEditPostPresenter>(), IEditPostView {
    override var presenter: IEditPostPresenter = EditPostPresenterIml()

    private var post: Post? = null
    private val postImageAdapter: PostImageAdapter = PostImageAdapter()
    private var currentNumImages = 0

    private var youtubeUrl: String = ""
    private var youtubeId: String = ""

    private var selectedTopic: Topic? = null
    private var topicDialog: Dialog? = null

    companion object {
        const val EDIT_POST_KEY_DATA = "EDIT_POST_KEY_DATA"

        fun start(fragment: Fragment, context: Context, post: Post) {
            val intent = Intent(context, EditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(EDIT_POST_KEY_DATA, post)
            intent.putExtras(bundle)
            fragment.startActivityForResult(intent, Constant.EDIT_POST_REQUEST)
        }

        fun start(activity: Activity, context: Context, post: Post) {
            val intent = Intent(context, EditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(EDIT_POST_KEY_DATA, post)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.EDIT_POST_REQUEST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_edit_post
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        bindingBasicInfo()
        if (intent.hasExtra(EDIT_POST_KEY_DATA)) {
            post = bundle?.getSerializable(EDIT_POST_KEY_DATA) as Post

            post?.let {
                setGAScreenName("Edit Post ${it.id}", GACategory.ACCOUNT)
                edit_post_nav.nav_new_tv_title.text = getString(R.string.edit_post)
                currentNumImages = it.photos.count()
                edit_post_et_content.setText(it.post_Content)

                fillSelectedTopic(post?.topic)
                fillYoutubeLink(post?.youtubeUrl ?: "")
            }
        }

        initRecycleView()

        edit_post_add_image.setOnClickListener {
            KeyboardUtil.forceHideKeyboard(this)
            showViewInputYoutubeLink(false)
            showImagePicker()
        }

        edit_post_add_youtube_link.setOnClickListener {
            val show = new_post_view_input_youtube.visibility
            showViewInputYoutubeLink(show == View.GONE)
        }

        edit_post_btn_done_youtube.setOnClickListener {
            val text = edit_post_edt_link_youtube.text.toString().trim()
            fillYoutubeLink(text)

            if (youtubeId.isNotEmpty() || text.isEmpty()) {
                KeyboardUtil.forceHideKeyboard(this)
                showViewInputYoutubeLink(false)
            }
            else {
                DialogUtil.showErrorDialog(this, "",
                    "Link Youtube không hợp lệ", getString(R.string.ok), null)
            }
        }

        edit_post_add_topic.setOnClickListener {
            KeyboardUtil.forceHideKeyboard(this)
            showViewInputYoutubeLink(false)
            showListTopics()
        }

        edit_post_selected_topic_delete.setOnClickListener {
            fillSelectedTopic(null)
        }

        edit_post_selected_youtube_link_delete.setOnClickListener {
            fillYoutubeLink("")
        }

        edit_post_nav.nav_tvCancel.setOnClickListener {
            finish()
        }

        edit_post_nav.nav_tvOK.setOnClickListener {
            editPost()
        }
    }

    private fun showImagePicker() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(this, CustomImageSelectorActivity::class.java)
        intent.putExtra(
            ImageSelectorActivity.EXTRA_MAX_SELECT_NUM,
            Constant.MAX_IMAGE_UPLOAD - currentNumImages
        )
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

    private fun showListTopics() {
        val topics = LHApplication.instance.lhCache.postTopics

        topicDialog =
            DialogBottomSheetUtil.showDialogListTopics(this, selectedTopic, topics, object :
                TopicItemHandler {
                override fun onClicked(topic: Topic) {
                    fillSelectedTopic(topic)
                    topicDialog?.dismiss()
                }
            })
    }

    private fun showViewInputYoutubeLink(show: Boolean) {
        edit_post_view_input_youtube.visibility = if (show) View.VISIBLE else View.GONE

        if (show) {
            edit_post_edt_link_youtube.requestFocus()
            KeyboardUtil.showKeyboard(this)
        } else {
            KeyboardUtil.forceHideKeyboard(this)
        }

        val colorId = if (show) R.color.colorAccent else R.color.color_black_60
        val color = Util.getColor(this, colorId)
        edit_post_add_youtube_link_image.setColorFilter(color)
        edit_post_add_youtube_link_title.setTextColor(color)
    }

    private fun bindingBasicInfo() {
        LocalStorage.user()?.let {
            edit_post_tv_full_name.text = it.name

            val tagName = "@" + it.username
            edit_post_tv_user_name.text = tagName

            LHPicasso.loadImage(it.avatar, edit_post_img_avatar)
        }
    }

    private fun fillSelectedTopic(topic: Topic?) {
        selectedTopic = topic
        edit_post_selected_topic_name.text = topic?.topic ?: ""
        edit_post_selected_topic.visibility = if (topic == null) View.GONE else View.VISIBLE
    }

    private fun fillYoutubeLink(link: String) {
        youtubeUrl = link
        youtubeId = getYoutubeIdFromLink(link)

        edit_post_selected_youtube_link_content.text = youtubeUrl
        edit_post_selected_youtube_link.visibility =
            if (youtubeId.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun getYoutubeIdFromLink(link: String): String {
        return ""
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)

        edit_post_recycler_view.layoutManager = layoutManager
        edit_post_recycler_view.isNestedScrollingEnabled = false
        edit_post_recycler_view.adapter = postImageAdapter

        postImageAdapter.listener = object : PostImageItemHandler {
            override fun onRemove(bitmap: BitmapUpload) {
                presenter.removeImage(bitmap)
                postImageAdapter.images.remove(bitmap)
                currentNumImages--
                postImageAdapter.notifyDataSetChanged()
            }
        }

        val bitmaps: MutableList<BitmapUpload> = ArrayList()
        post?.let {
            if (it.photos.isNotEmpty()) {
                for (photo in it.photos) {
                    val bitmapUpload = BitmapUpload()
                    bitmapUpload.photo = photo
                    bitmapUpload.url = photo.photo_Link
                    bitmapUpload.uploaded = true
                    bitmaps.add(bitmapUpload)
                }
            }
        }

        postImageAdapter.images.addAll(bitmaps)
        postImageAdapter.notifyDataSetChanged()
    }

    private fun editPost() {
        val content = edit_post_et_content.text

        if (content.isEmpty() && postImageAdapter.images.isEmpty() && youtubeId.isEmpty()) {
            DialogUtil.showErrorDialog(
                this,
                getString(R.string.new_post_error_title),
                getString(R.string.new_post_error_empty_input),
                getString(R.string.ok), null
            )
            return
        }

        edit_post_progress.visibility = View.VISIBLE
        post?.let {
            presenter.editPost(
                it.id,
                content.toString(),
                youtubeId,
                selectedTopic?.id ?: 0,
                ArrayList(postImageAdapter.images)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK &&
            requestCode == ImageSelectorActivity.REQUEST_IMAGE
        ) {
            data?.let {
                val images = it.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT)
                        as ArrayList<String>

                val bitmaps: MutableList<BitmapUpload> = ArrayList()
                images.mapTo(bitmaps) {
                    BitmapUpload(Date().time, BitmapUtil.getBitmapFromFile(it))
                }
                currentNumImages += bitmaps.size

                postImageAdapter.images.addAll(bitmaps)
                postImageAdapter.notifyDataSetChanged()
                post?.let { post ->
                    presenter.uploadImages(post.id, ArrayList(bitmaps))
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun submitPostCompleted(post: Post) {
        val intent = Intent(this, NewPostActivity::class.java)
        intent.putExtra(EDIT_POST_KEY_DATA, post)

        setResult(Constant.EDIT_POST_REQUEST, intent)

        edit_post_progress.visibility = View.GONE
        finish()
    }

    override fun submitPostFailed() {
        DialogUtil.showErrorDialog(
            this,
            getString(R.string.edit_post_error_title),
            getString(R.string.new_post_error_desc),
            getString(R.string.ok), null
        )
        edit_post_progress.visibility = View.GONE
    }
}
