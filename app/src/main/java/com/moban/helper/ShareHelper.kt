package com.moban.helper

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.moban.model.data.post.Post

class ShareHelper {

    companion object {

        fun sharePost(context: Context, post: Post) {
            val url = post.web_Url
            if (url.isNotEmpty()) {
                shareString(context, url)
            }
        }

        private fun shareString(context: Context, content: String) {
            try {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, content)
                context.startActivity(Intent.createChooser(sharingIntent, "Chia sẻ qua"))
            } catch (exception: Exception) {
                Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }
        }
    }
}