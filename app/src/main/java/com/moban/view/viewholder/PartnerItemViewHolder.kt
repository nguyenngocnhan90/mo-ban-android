package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.user.User
import com.moban.util.Font
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_partner.view.*

/**
 * Created by LenVo on 5/11/18.
 */
class PartnerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(user: User) {
        val context = itemView.context
        val fontRegular = Font.regularTypeface(context)

        itemView.item_partner_tv_name.typeface = fontRegular
        itemView.item_partner_tv_name.text = user.name

        LHPicasso.loadImage(user.avatar, itemView.item_partner_img_avatar)
    }

}
