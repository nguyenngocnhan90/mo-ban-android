package com.moban.view.viewholder

import android.content.Context
import android.graphics.Typeface
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.RecyclerView
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.Gson
import com.moban.R
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.FeedItemHandler
import com.moban.handler.FeedMenuHandler
import com.moban.handler.IYoutubeContainer
import com.moban.helper.CustomTypefaceSpan
import com.moban.helper.LocalStorage
import com.moban.model.data.media.Photo
import com.moban.model.data.news.News
import com.moban.model.data.post.Post
import com.moban.util.*
import com.moban.view.custom.ImageViewerOverlay
import com.moban.view.custom.imageviewer.ImageViewer
import com.squareup.picasso.Picasso
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.item_feed.view.*
import java.lang.Math.min


/**
 * Created by neal on 3/11/18.
 */

class FeedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener: FeedItemHandler? = null
    var dialogFeedMenu: BottomSheetDialog? = null

    fun bind(post: Post, position: Int, listener: FeedItemHandler?) {
        val context = itemView.context
        this.listener = listener

        arrayOf(itemView.item_feed_tv_createdTime,
                itemView.item_feed_tv_number_of_likes,
                itemView.item_feed_tv_number_of_comments,
                itemView.item_feed_tv_like,
                itemView.item_feed_tv_comment,
                itemView.item_feed_tv_share,
                itemView.item_feed_new_tv_short_description)
                .forEach {
                    it.typeface = Font.regularTypeface(context)
                }

        LHPicasso.loadImage(post.avatar, itemView.item_feed_img_avatar)

        stylePostTitle(post)

        // Created time
        itemView.item_feed_tv_createdTime.text = DateUtil.getPastTimeString(post.created_Date.toLong(), context)

        itemView.item_feed_new_post.visibility = if (post.type == 3) View.VISIBLE else View.GONE
        itemView.item_feed_normal_post.visibility = if (post.type != 3) View.VISIBLE else View.GONE
        if (post.type == 3) {
            bindingNewPost(post, context)
        } else {
            bindingNormalPost(post, context)
        }
        // React status
        itemView.item_feed_llShare.visibility = if (post.web_Url.isNotEmpty()) View.VISIBLE else View.GONE

        val likeCount = post.likes
        val commentCount = post.comments
        val isLiked = post.is_Liked

        itemView.item_feed_rltReactInfo.visibility = if (likeCount == 0 && commentCount == 0)
            View.GONE else View.VISIBLE
        itemView.item_feed_imgLoveCount.visibility = if (likeCount == 0) View.GONE else View.VISIBLE

        itemView.item_feed_tv_number_of_likes.text = if (likeCount == 0) "" else
            likeCount.toString() + " Thích"
        itemView.item_feed_tv_number_of_comments.text = if (commentCount == 0) "" else
            commentCount.toString() + " Bình Luận"

        val likeIcon = if (isLiked) R.drawable.post_liked else R.drawable.post_like
        itemView.item_feed_imgLike.setImageResource(likeIcon)
    }

    private fun bindingNormalPost(post: Post, context: Context) {
// Content
        itemView.item_feed_tv_content.isTrim = !post.isExpanded
        itemView.item_feed_tv_content.trimLength = if (post.isExpanded) 0 else 270
//        itemView.item_feed_tv_content.text = post.post_Content.trim().replace("[\n\r", "")
        stylePostContent(post)
        // Video
        val isVideo = post.hasVideo()

        itemView.item_feed_view_video.visibility = if (isVideo) View.VISIBLE else View.GONE
        itemView.item_feed_btn_play.visibility = if (isVideo) View.VISIBLE else View.GONE

        // List photo
        val photos = post.photos
        val count = photos.count()
        itemView.item_feed_main_image.visibility = if (count == 1 || isVideo)
            View.VISIBLE else View.GONE
        itemView.item_feed_view_list_images.visibility = if (count > 1 && !isVideo)
            View.VISIBLE else View.GONE

        if (isVideo) {
            val mainImagesLayoutParams = itemView.item_feed_main_image.layoutParams
            val width = Device.getScreenWidth() - 24
            val height = width * 9 / 16

            mainImagesLayoutParams.height = height
            itemView.item_feed_main_image.layoutParams = mainImagesLayoutParams

            val videoViewLayoutParam = itemView.item_feed_view_video.layoutParams
            videoViewLayoutParam.height = height
            itemView.item_feed_view_video.layoutParams = videoViewLayoutParam

            post.video?.let { video ->
                LHPicasso.loadImage(video.thumb_Image, itemView.item_feed_main_image)

                itemView.item_feed_btn_play.setOnClickListener { _ ->
                    itemView.item_feed_btn_play.visibility = View.GONE

                    video.youtube_Id?.let {
                        attachAndPlayYoutubeVideo(it, position)
                    }
                }
            }
        } else if (!photos.isEmpty()) {
            val maxCount = min(3, count)

            val listLayoutParams = itemView.item_feed_view_list_images.layoutParams
            val width = Device.getScreenWidth()

            if (count == 1) {
                photos.first().let {
                    it.photo_Size?.let { size ->
                        itemView.item_feed_main_image.viewTreeObserver.addOnGlobalLayoutListener {
                            val mainImagesLayoutParams = itemView.item_feed_main_image.layoutParams
                            if (size.width > 0) {
                                val height = itemView.item_feed_main_image.width * size.height / size.width
                                mainImagesLayoutParams.height = height
                            }
                            mainImagesLayoutParams.width = width
                            itemView.item_feed_main_image.layoutParams = mainImagesLayoutParams
                        }
                    }

                    LHPicasso.loadImage(it.photo_Link, itemView.item_feed_main_image)

                    itemView.item_feed_main_image.setOnClickListener {
                        handleImageClick(context, photos, 0)
                    }
                }
            } else {
                val height = (width - 2 * (maxCount - 1)) / maxCount
                listLayoutParams.height = height
                itemView.item_feed_view_list_images.layoutParams = listLayoutParams

                val imageViews = arrayOf(
                        itemView.item_feed_list_image_1,
                        itemView.item_feed_list_image_2,
                        itemView.item_feed_list_image_3)
                imageViews.forEachIndexed { index, imageView ->
                    imageView.setOnClickListener {
                        handleImageClick(context, photos, index)
                    }

                    if (index < 2) {
                        imageView.visibility = if (index < maxCount) View.VISIBLE else View.GONE
                    }

                    itemView.item_feed_rlt_image_3.visibility = if (maxCount == 3)
                        View.VISIBLE else View.GONE
                    itemView.item_feed_rlt_image_3.setOnClickListener {
                        handleImageClick(context, photos, 3)
                    }

                    if (index < maxCount) {
                        val url = photos[index].photo_Link

                        if (url.isNotBlank()) {
                            Picasso.with(context).load(url)
                                    .placeholder(R.color.color_light_gray)
                                    .fit().centerCrop()
                                    .into(imageView)
                        }
                    }
                }

                itemView.item_feed_tv_more_image.visibility = if (count > 3) View.VISIBLE else View.GONE
                if (count > 3) {
                    itemView.item_feed_tv_more_image.text = "+" + (count - 2).toString()
                }
            }
        }
    }

    private fun bindingNewPost(post: Post, context: Context) {
        val newPost = Gson().fromJson(post.post_Content, News::class.java)
        itemView.item_feed_new_tv_title.text = newPost.title
        itemView.item_feed_new_tv_short_description.text = newPost.domain
        val photos = post.photos
        if (photos.isNotEmpty()) {
            photos.first().let {
                val mainImagesLayoutParams = itemView.item_feed_new_main_image.layoutParams
                val width = Device.getScreenWidth()
                mainImagesLayoutParams.height = width * 9 / 16
                itemView.item_feed_new_main_image.layoutParams = mainImagesLayoutParams

                if (!it.photo_Link.isNullOrBlank()) {
                    LHPicasso.loadImage(it.photo_Link, itemView.item_feed_new_main_image)
                }
            }
        }

        newPost.url?.let { url ->
            itemView.setOnClickListener {
                WebViewActivity.start(context, newPost.title, url)
            }
        }
    }

    private fun stylePostContent(post: Post) {
        val context = itemView.context
        val fontRegular = Typeface.createFromAsset(context.assets,
                context.resources.getString(R.string.font_regular))

        val spannable = SpannableString(post.getFullContent())
        val urlMatcher = Patterns.WEB_URL.matcher(post.getFullContent())
        while (urlMatcher.find()) {
            val urlSpan = urlMatcher.group()
            if (urlSpan != null && urlSpan.contains("http", true)) {
                val spanStart = urlMatcher.start(0)
                val spanEnd = urlMatcher.end(0)
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        listener?.onClickURL(urlSpan)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds?.color = Util.getColor(context, R.color.colorPrimary)
                        ds?.isUnderlineText = false
                    }
                }, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.removeSpan(urlSpan)
            }
        }

        itemView.item_feed_tv_content.text = spannable
        itemView.item_feed_tv_content.typeface = fontRegular
        itemView.item_feed_tv_content.isClickable = true
        itemView.item_feed_tv_content.movementMethod = LinkMovementMethod.getInstance()

        itemView.item_feed_tv_content.setOnLongClickListener {
            val tooltip = SimpleTooltip.Builder(context)
                    .anchorView(itemView.item_feed_tv_content)
                    .gravity(Gravity.TOP)
                    .dismissOnOutsideTouch(true)
                    .dismissOnInsideTouch(false)
                    .modal(true)
                    .text(context.getString(R.string.copy))
                    .contentView(R.layout.copy_tooltip, R.id.tv_text)
                    .focusable(false)
                    .arrowColor(Util.getColor(context, R.color.colorPrimaryDark))
                    .arrowHeight(20f)
                    .arrowWidth(50f)
                    .build()
            tooltip.show()
            val text = tooltip.findViewById(R.id.tv_text) as TextView
            text.setOnClickListener {
                Util.saveToClipboard(context, post.getFullContent())
                tooltip.dismiss()
            }
            true
        }
    }

    private fun stylePostTitle(post: Post) {
        val context = itemView.context
        val fontBold = Typeface.createFromAsset(context.assets,
                context.resources.getString(R.string.font_bold))
        val fontRegular = Typeface.createFromAsset(context.assets,
                context.resources.getString(R.string.font_regular))

        // Title
        val owner = post.user_Profile_Name?.capitalize()
        val projects = post.post_Product_Tags
        val projectCount = projects.count()

        var text = owner + " đã đăng bài viết"
        var firstProjectName = ""
        var moreProjectsString = ""

        var indexFirstProject = 0
        var indexMoreProjects = 0

        if (projectCount > 0) {
            firstProjectName = projects.first().product_Name
            text += " cho dự án "

            indexFirstProject = text.length
            text += firstProjectName

            if (projectCount > 1) {
                moreProjectsString = (projectCount - 1).toString() + " dự án khác"
            }

            if (!moreProjectsString.isEmpty()) {
                text += " và "

                indexMoreProjects = text.length
                text += moreProjectsString
            }
        }

        text += "."

        val descriptionBuilder = SpannableStringBuilder(text)
        var userDescriptionLength = 0
        owner?.let { userDescriptionLength = it.length }

        descriptionBuilder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                listener?.onViewProfileDetail(post.user_Id)
            }

            override fun updateDrawState(ds: TextPaint) {
                val oldStyle: Int
                val old = ds?.typeface
                oldStyle = if (old == null) {
                    0
                } else {
                    old.style
                }

                val fake = oldStyle and fontBold.style.inv()
                if (fake and Typeface.BOLD != 0) {
                    ds?.isFakeBoldText = true
                }

                if (fake and Typeface.ITALIC != 0) {
                    ds?.textSkewX = -0.25f
                }

                ds?.typeface = fontBold
                ds?.isUnderlineText = false
            }
        }, 0, userDescriptionLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        descriptionBuilder.setSpan(CustomTypefaceSpan("", fontBold), 0, userDescriptionLength,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        // For first project
        if (!firstProjectName.isEmpty()) {
            val length = firstProjectName.length
            val end = length + indexFirstProject

            descriptionBuilder.setSpan(CustomTypefaceSpan("", fontBold), indexFirstProject, end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        // For more project
        if (!moreProjectsString.isEmpty()) {
            val length = moreProjectsString.length
            val end = length + indexMoreProjects

            descriptionBuilder.setSpan(CustomTypefaceSpan("", fontBold), indexMoreProjects, end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        itemView.item_feed_tv_user.typeface = fontRegular
        itemView.item_feed_tv_user.text = descriptionBuilder
        itemView.item_feed_tv_user.isClickable = true
        itemView.item_feed_tv_user.movementMethod = LinkMovementMethod.getInstance()

        var isOwner = false
        LocalStorage.user()?.let {
            isOwner = (it.id == post.user_Id)
        }

        itemView.item_feed_btn_menu.setOnClickListener {

            dialogFeedMenu = DialogBottomSheetUtil.showDialogFeedOption(post.is_Followed, isOwner, context, object : FeedMenuHandler {
                override fun onClickedDelete() {
                    handleDelete(post)
                }

                override fun onClickedEdit() {
                    handleEdit(post)
                }

                override fun onClickedNotify() {
                    handleNotify(post)
                }
            })
        }
    }

    private fun handleNotify(post: Post) {
        listener?.onFollow(post)
        dialogFeedMenu?.dismiss()
    }

    private fun handleEdit(post: Post) {
        listener?.onEdit(post)
        dialogFeedMenu?.dismiss()
    }

    private fun handleDelete(post: Post) {
        listener?.onDelete(post)
        dialogFeedMenu?.dismiss()
    }

    private fun handleImageClick(context: Context, photos: List<Photo>, index: Int) {
        val overlay = ImageViewerOverlay(context)
        var currentImage = index

        val imageViewer = ImageViewer.Builder<Photo>(context, photos)
                .setFormatter { it.photo_Link }
                .setStartPosition(index)
                .setOverlayView(overlay)
                .setImageChangeListener { position ->
                    currentImage = position
                }
                .setOnTouchImageListener {
                    if (overlay.buttonDownload?.visibility == View.VISIBLE) {
                        overlay.buttonDownload?.visibility = View.INVISIBLE
                        Log.i("ImageView", "Invisible")
                    }
                }
                .setOnLongTouchImageListener {
                    overlay.visibility = View.VISIBLE
                    overlay.buttonDownload?.visibility = View.VISIBLE
                    overlay.requestLayout()
                    Log.i("ImageView", "Visible")
                }
                .build()
        imageViewer.show()

        overlay.buttonClose?.setOnClickListener {
            imageViewer.onDismiss()
        }

        overlay.buttonDownload?.setOnClickListener {
            listener?.onDownloadPhoto(photos[currentImage])
            overlay.buttonDownload?.visibility = View.INVISIBLE
        }
    }

    private fun attachAndPlayYoutubeVideo(youtubeId: String, position: Int) {
        val context = itemView.context as? IYoutubeContainer ?: return

        val youtubeContainer = context as IYoutubeContainer

        // send Google Analytics
        youtubeContainer.sendLoggingEvent("Play video - $youtubeId")

        // handle attach
        val flContainer = youtubeContainer.getFlVideoContainer()
        if (flContainer.parent != null) {
            (flContainer.parent as ViewGroup).removeView(flContainer)
        }

        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER

        itemView.item_feed_video_frame.addView(flContainer, layoutParams)
        itemView.item_feed_video_frame.visibility = View.VISIBLE
        flContainer.visibility = View.VISIBLE
        youtubeContainer.setViewYoutubePos(position)

        // for play
        youtubeContainer.getYoutubeVideoFragment().setAutoFullScreen(false)
        youtubeContainer.getYoutubeVideoFragment().onFullscreenListener = YouTubePlayer.OnFullscreenListener { isFullscreen -> youtubeContainer.handleOnVideoFullScreen(isFullscreen) }
        youtubeContainer.getYoutubeVideoFragment().loadVideoId(youtubeId)
        youtubeContainer.getYoutubeVideoFragment().setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
            override fun onPlaying() {
                // need to hide all view above this player
                youtubeContainer.checkYouTubeViewOverlay()
            }

            override fun onPaused() {}

            override fun onStopped() {}

            override fun onBuffering(b: Boolean) {}

            override fun onSeekTo(i: Int) {}
        })
        // apply full screen
        // doing here
    }
}
