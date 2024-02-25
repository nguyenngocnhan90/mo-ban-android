package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.gift.Gift
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_gift.view.*

/**
 * Created by LenVo on 4/11/18.
 */
class GiftItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(gift: Gift) {
        val context = itemView.context

        LHPicasso.loadImage(gift.gift_Image, itemView.item_gift_img)

        itemView.item_gift_tv_name.text = gift.gift_Name

        val coin = gift.gift_Point.toString() + " " + context.getString(R.string.lhc)
        itemView.item_gift_tv_coin.text = coin
    }

}
