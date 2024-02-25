package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.flow.profile.ProfileActivity
import com.moban.helper.LocalStorage
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_feed_welcome.view.*

class FeedWelcomeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind() {
        val context = itemView.context

        LocalStorage.user()?.let {
            val isAnonymous = it.isAnonymous()
            itemView.item_feed_welcome_img_avatar.visibility = if (isAnonymous) View.INVISIBLE else View.VISIBLE
            itemView.item_feed_welcome_btn_login.visibility = if (!isAnonymous) View.INVISIBLE else View.VISIBLE

            LHPicasso.loadImage(it.avatar, itemView.item_feed_welcome_img_avatar)
            itemView.item_feed_welcome_name.text = it.name
        }

        itemView.setOnClickListener {
            val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
            if (!isAnonymous) {
                ProfileActivity.start(context)
            }
        }
    }
}