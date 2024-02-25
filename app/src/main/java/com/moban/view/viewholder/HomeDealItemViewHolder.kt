package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.adapter.policy.PolicyDocumentAdapter
import com.moban.enum.HomeDealType
import com.moban.flow.homedeal.HomeDealActivity
import com.moban.model.data.homedeal.HomeDeal
import com.moban.util.BitmapUtil
import com.moban.util.DateUtil
import com.moban.util.Util
import com.moban.view.custom.CountDownView
import kotlinx.android.synthetic.main.item_feed_new_deal.view.*

/**
 * Created by LenVo on 7/19/18.
 */
class HomeDealItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val documentAdapter: PolicyDocumentAdapter = PolicyDocumentAdapter()

    fun bind(homeDeal: HomeDeal, listener: CountDownView.Handler? = null) {
        val context = itemView.context
        val title = homeDeal.title
        itemView.feed_new_deal_tv_name.text = title

        val type = HomeDealType.from(homeDeal.type)
        val isFlashSale = type == HomeDealType.flashsale
        itemView.feed_new_deal_view_flashsale.visibility = if(isFlashSale) View.VISIBLE else View.GONE
        itemView.feed_new_deal_tv_name.visibility = if(isFlashSale) View.GONE else View.VISIBLE

        if (isFlashSale) {
            val remainingTime = homeDeal.endTime - DateUtil.currentTimeSeconds()
            itemView.feed_new_deal_view_countdown.seconds = remainingTime.toLong()
            itemView.feed_new_deal_view_countdown.reloadData()
            itemView.feed_new_deal_view_countdown.listener = object: CountDownView.Handler {
                override fun onTimeOut() {
                    listener?.onTimeOut()
                }
            }
        }

        itemView.feed_new_deal_btn_all.setOnClickListener {
            HomeDealActivity.start(context, homeDeal)
        }

        val isSpecialDeal = type == HomeDealType.special
        val isFlashSaleDeal = type == HomeDealType.flashsale

        itemView.feed_new_deal_img_special_tag.visibility = if (isSpecialDeal) View.VISIBLE else View.GONE
        if (isSpecialDeal) {
            itemView.feed_new_deal_tv_name.setTextColor(Util.getColor(context, R.color.white))
            itemView.feed_new_deal_btn_all.setTextColor(Util.getColor(context, R.color.color_btn_special_deal))
            itemView.background = context.getDrawable(R.drawable.special_deal_bg)

            //Limit height for special deals
            val param = itemView.layoutParams
            param.height = BitmapUtil.convertDpToPx(itemView.context, 320)
            itemView.layoutParams = param

            itemView.feed_new_deal_btn_all.setTextColor(Util.getColor(context, R.color.white))
            itemView.feed_new_deal_btn_all_image.setColorFilter(Util.getColor(context, R.color.white))
        }

        val deals = homeDeal.deals
        if (deals.isNullOrEmpty()) {
            return
        }

        var layoutManager = GridLayoutManager(context, 2,LinearLayoutManager.HORIZONTAL, false)
        if (deals.size <= 2 || isSpecialDeal) {
            layoutManager = GridLayoutManager(context, 1,LinearLayoutManager.HORIZONTAL, false)
        }
        itemView.item_feed_new_deal_recycle_view.layoutManager = layoutManager
        itemView.item_feed_new_deal_recycle_view.adapter = documentAdapter
        documentAdapter.isSpecialDeal = isSpecialDeal
        documentAdapter.isFlashSaleDeal = isFlashSaleDeal
        documentAdapter.setDocumentList(deals.toMutableList())
    }

}
