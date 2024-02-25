package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.model.data.user.User
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_birthday.view.*
import kotlinx.android.synthetic.main.item_photo_full_width.view.*

/**
 * Created by LenVo on 6/18/18.
 */
class UserItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(user: User) {
        itemView.item_birthday_tv_name.text = user.name
        LHPicasso.loadImage(user.avatar, itemView.item_birthday_img_avatar)
    }

    fun bindTopPartner(user: User) {
        LHPicasso.loadImage(user.avatar, itemView.item_photo_img)
    }

}
