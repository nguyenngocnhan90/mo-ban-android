package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.user.User
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_feed_new_post.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class NewPostItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(user: User) {
        LHPicasso.loadImage(user.avatar, itemView.home_new_post_img_avatar)
    }

}
