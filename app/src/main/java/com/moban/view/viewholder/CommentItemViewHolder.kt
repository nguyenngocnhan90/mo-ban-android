package com.moban.view.viewholder

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.adapter.feed.FeedSubCommentAdapter
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.util.DateUtil
import com.moban.util.LHPicasso
import com.moban.view.custom.ImageViewerOverlay
import com.moban.view.custom.imageviewer.ImageViewer
import kotlinx.android.synthetic.main.item_comment.view.*

/**
 * Created by neal on 3/15/18.
 */
class CommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val subCommentAdapter = FeedSubCommentAdapter()

    fun bind(comment: Comment) {
        val context = itemView.context
        val fontBold = Typeface.createFromAsset(
            context.assets,
            context.resources.getString(R.string.font_bold)
        )

        LHPicasso.loadAvatar(comment.avatar, itemView.item_comment_img_avatar)

        // Created time
        itemView.item_comment_tv_time.text =
            DateUtil.getPastTimeString(comment.created_Date.toLong(), context)

        // Username
        itemView.item_comment_tv_username.typeface = fontBold
        itemView.item_comment_tv_username.text = comment.user_Profile_Name

        // Content
        val content = comment.comment_Content
        if (content.isNotEmpty()) {
            itemView.item_comment_tv_comment.text = content.trim()
                .replace("[\n\r", "")
        }
        itemView.item_comment_tv_comment.visibility =
            if (content.isEmpty()) View.GONE else View.VISIBLE

        // Image
        val photo = comment.photo
        val imageUrl = photo?.photo_Link ?: ""
        itemView.item_comment_image_view_card.visibility =
            if (imageUrl.isEmpty()) View.GONE else View.VISIBLE
        if (imageUrl.isNotEmpty()) {
            val width = photo?.width ?: 0
            val height = photo?.height ?: 0

            if (height > 0) {
                val layoutParam = itemView.item_comment_image_view_card.layoutParams
                layoutParam.width = width * layoutParam.height / height
                itemView.item_comment_image_view_card.layoutParams = layoutParam
            }

            LHPicasso.loadImage(imageUrl, itemView.item_comment_image_view)
        }

        itemView.item_comment_image_view_card.setOnClickListener {
            photo?.let { photo ->
                handleImageClick(context, photo)
            }
        }

        // Sub comments
        val linearLayoutManager = LinearLayoutManager(context)
        itemView.item_comment_recycler_view_comments.layoutManager = linearLayoutManager

        itemView.item_comment_recycler_view_comments.adapter = subCommentAdapter
        subCommentAdapter.comments = comment.sub_Comments ?: ArrayList()
    }

    private fun handleImageClick(context: Context, photo: Photo, index: Int = 0) {
        val overlay = ImageViewerOverlay(context)
        var currentImage = index

        val photos = arrayOf(photo)
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

        overlay.buttonDownload?.visibility = View.INVISIBLE
    }
}
