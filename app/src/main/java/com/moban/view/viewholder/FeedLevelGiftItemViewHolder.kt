package com.moban.view.viewholder

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.flow.linkmart.detail.LinkmartDetailActivity
import com.moban.model.data.user.LevelGift
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_new_deal.view.*

class FeedLevelGiftItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(gift: LevelGift) {
        val context = itemView.context
        itemView.item_new_deal_tv_title.text = gift.gift_Name
        LHPicasso.loadImage(gift.gift_Image, itemView.item_new_deal_img_gift)
        val price = gift.gift_Value.toString() + " VNĐ"
        itemView.item_new_deal_tv_price.text = price

        val hasSalePrice = gift.gift_Value_Deal != null

        val regularPrice = gift.gift_Value.toString() + " VNĐ" + if (hasSalePrice) " - " else ""
        val spannablePrice =  SpannableString(regularPrice)
        if (hasSalePrice) {
            spannablePrice.setSpan(StrikethroughSpan(), 0, regularPrice.length - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val salePrice = gift.gift_Value_Deal?.toString() + " VNĐ"
            itemView.item_new_deal_tv_deal_price.text = salePrice
        }
        itemView.item_new_deal_tv_price.text = spannablePrice
        itemView.setOnClickListener {
            LinkmartDetailActivity.start(context, gift)
        }
    }
}
