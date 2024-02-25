package com.moban.view.viewholder

import android.graphics.Color
import android.os.CountDownTimer
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.enum.ApproveStatus
import com.moban.handler.DealItemHandler
import com.moban.model.data.deal.Deal
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_deal_project.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class DealProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var countDownTimer: CountDownTimer? = null

    fun bind(deal: Deal, isSameProject: Boolean, listener: DealItemHandler?) {
        val context = itemView.context
        LHPicasso.loadImage(deal.product_Image, itemView.item_deal_img_logo)
        itemView.item_deal_tv_project_name.text = deal.product_Name
        itemView.item_deal_view_project_name.visibility = if (isSameProject) View.GONE else View.VISIBLE

        val title = deal.customer_Name_Exact
        itemView.item_deal_tv_name.text = title
        itemView.item_deal_tv_price.text = deal.deal_Price_String
        itemView.item_deal_tv_date.text = deal.deal_Date_String
        itemView.item_deal_tv_order_status.text = deal.deal_Status_String
        itemView.item_deal_tv_order_status.setTextColor(Color.parseColor(deal.deal_Status_Color))

        val index = deal.booking_Index
        val isInvalid = index == 0 || deal.approve_Status != ApproveStatus.Approved.value
        itemView.item_deal_tv_order_num.text = if (isInvalid) "" else index.toString()
        itemView.item_deal_view_order_num.visibility = if (isInvalid) View.GONE else View.VISIBLE

        if (deal.hasComment()) {
            itemView.item_deal_tv_reason.visibility = View.VISIBLE
            val reason = context.getString(R.string.reason_reject) + " " + deal.deal_Comment
            itemView.item_deal_tv_reason.text = reason
        }

        var titleId = R.string.call_support
        if (deal.canDeposit()) {
            titleId = R.string.deal_change_to_coc
        }
        else if (deal.canMakeContract()) {
            titleId = R.string.deal_change_to_contract
        }
        itemView.item_deal_btn_main_action.text = context.getString(titleId)

        val countdownVisibility = if (deal.isInFixingTime()) View.VISIBLE else View.GONE
        itemView.item_deal_view_remain_time.visibility = countdownVisibility
        itemView.item_deal_view_circle_time.visibility = countdownVisibility

        if (deal.isInFixingTime()) {
            countDownTimer?.cancel()
            countdown(deal.remainingFixingSecond(), deal, listener)
        }
    }

    private fun countdown(sec: Double, deal: Deal, listener: DealItemHandler?) {
        itemView.item_deal_view_circle_time.visibility = View.VISIBLE
        itemView.item_deal_lock.visibility = View.GONE

        itemView.item_deal_circle_count_down.max = 15*60

        countDownTimer = object : CountDownTimer((sec * 1000).toLong(), 1000) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds / 1000
                itemView.item_deal_circle_count_down.progress = seconds.toInt()
                val secString = deal.remainingFixingTimeString()
                itemView.item_deal_tv_timer.text = secString
            }

            override fun onFinish() {
                listener?.onTimeOut()
            }
        }
        countDownTimer?.start()
    }
}
