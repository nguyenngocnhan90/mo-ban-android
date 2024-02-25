package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.user.User
import com.moban.util.LHPicasso
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_rank.view.*

/**
 * Created by LenVo on 6/21/18.
 */
class RankItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    fun bind(user: User, position: Int, isPartnerManager: Boolean) {
        val context = itemView.context
        itemView.setBackgroundColor(if (position%2 == 0) Util.getColor(context, R.color.white)
                                    else Util.getColor(context, R.color.color_gray_background_50))
        itemView.item_rank_tv_index.text = (position + 1).toString()
        itemView.item_rank_tv_name.text = user.name
        itemView.item_rank_tv_branch_name.text = user.branch
        itemView.item_rank_tv_level.text = user.rankLevelNumber(isPartnerManager)

        LHPicasso.loadAvatar(user.avatar, itemView.item_rank_img_avatar)
    }

}
