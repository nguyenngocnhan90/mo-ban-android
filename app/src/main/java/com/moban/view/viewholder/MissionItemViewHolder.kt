package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.user.Mission
import com.moban.util.Font
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_mission.view.*

class MissionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(mission: Mission) {
        val context = itemView.context

        itemView.item_mission_tv_coin.typeface = Font.mediumTypeface(context)
        itemView.item_mission_tv_name.typeface = Font.regularTypeface(context)

        itemView.item_mission_tv_coin.text = mission.linkCoin.toString() + " " + context.getString(R.string.lhc)
        itemView.item_mission_tv_name.text = mission.mission_Name

        LHPicasso.loadImage(mission.badge_Image, itemView.item_mission_img_badge)
    }
}
