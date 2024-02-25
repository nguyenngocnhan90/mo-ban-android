package com.moban.view.viewholder

import android.graphics.Paint
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.adapter.project.ProjectAdapter
import com.moban.enum.ProjectStatus
import com.moban.model.data.project.Project
import com.moban.util.*
import kotlinx.android.synthetic.main.item_project.view.*

/**
 * Created by neal on 3/17/18.
 */
class ProjectItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(project: Project, position: Int, type: Int) {
        val context = itemView.context

        if (type == ProjectAdapter.HOT_PROJECT_HORIZONTAL) {
            itemView.setBackgroundColor(Util.getColor(context, R.color.color_text_field_bg))
        }

        if (type == ProjectAdapter.PROJECT_DETAIL_HORIZONTAL) {
            itemView.setBackgroundColor(Util.getColor(context, R.color.white))
        }

        val width = if (type == ProjectAdapter.PROJECT_VERTICAL) Device.getScreenWidth() else
            Device.getScreenWidth() - BitmapUtil.convertDpToPx(context, 30)

        val marginStart = if (type == ProjectAdapter.PROJECT_VERTICAL) 0 else
            BitmapUtil.convertDpToPx(context, 10)

        val layoutParam = RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.marginStart = marginStart
        if (type == ProjectAdapter.PROJECT_VERTICAL) {
            layoutParam.topMargin = BitmapUtil.convertDpToPx(itemView.context, if (position == 0) 10 else 0)
        }
        itemView.layoutParams = layoutParam

        val fontRegular = Font.regularTypeface(context)

        arrayOf(itemView.item_project_tv_type_project, itemView.item_project_tvAgentRate,
                itemView.item_project_tvAddr)
                .forEach {
                    it.typeface = fontRegular
                }

        itemView.item_project_tvName.text = project.product_Name
        itemView.item_project_tvAddr.text = project.fullAddress()
        itemView.item_project_bottom_line.visibility = if (type == ProjectAdapter.PROJECT_VERTICAL) View.VISIBLE else
            View.GONE

        val discountFee = project.finalDiscountServiceFee()
        val fee = project.finalServiceFee()
        itemView.item_project_tv_service_fee.text = fee
        itemView.item_project_tv_discount_service_fee.text = discountFee

        itemView.item_project_tv_service_fee_separator.visibility = if (fee.isEmpty()) View.INVISIBLE else View.VISIBLE
        itemView.item_project_tv_service_fee.paintFlags = itemView.item_project_tv_service_fee.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        val statusName = project.product_Status
        val status = ProjectStatus.from(project.product_Status_Id)

        val isShowStatus = status != null && status != ProjectStatus.none && statusName.isNotEmpty() && type == ProjectAdapter.PROJECT_VERTICAL
        itemView.item_project_tv_type_project.visibility = if (isShowStatus) View.VISIBLE else View.GONE
        itemView.item_project_tv_type_project.text = statusName
        status?.let {
            itemView.item_project_tv_type_project.background = status.color(context)
        }

        itemView.item_project_tv_advance_rate.text = project.advance_Rate
        itemView.item_project_tv_commission_rate.text = project.commission_Rate_Agent
        itemView.item_project_tv_deal_price.text = project.deal_Price

        val agentRate = project.product_Agent_Rate_Text
        val hasNoAgentRate = agentRate.isEmpty()
        itemView.item_project_tvAgentRate.text = agentRate
        itemView.item_project_viewAgentRate.visibility = if (hasNoAgentRate) View.INVISIBLE else View.VISIBLE

        LHPicasso.loadImage(project.product_Image, itemView.item_project_imgMedia)

        val param = itemView.item_project_imgMedia.layoutParams
        param.height = Device.getScreenWidth() * 9 / 16
        itemView.item_project_imgMedia.layoutParams = param
    }

}
