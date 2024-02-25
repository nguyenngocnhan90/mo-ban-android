package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.moban.R
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.util.BitmapUtil
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_secondary_house.view.*

/**
 * Created by LenVo on 3/11/19.
 */
class SecondaryHouseItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(house: SecondaryHouse, position: Int) {
        val context = itemView.context
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.topMargin = BitmapUtil.convertDpToPx(itemView.context, if (position == 0) 10 else 0)
        itemView.layoutParams = layoutParam

        itemView.item_secondary_project_tvName.text = house.contract_code
        itemView.item_secondary_project_tvAddr.text = house.fullAddress()
        itemView.item_secondary_project_tv_type_project.text = house.docquyen
        itemView.item_secondary_project_tv_type_project.visibility = if(house.docquyen.isNullOrEmpty()) View.GONE else View.VISIBLE


        itemView.item_secondary_project_tvAgentRate.text = house.price
        itemView.item_secondary_project_tvAgentRateTitle.visibility = if (house.price.isEmpty())
            View.INVISIBLE else View.VISIBLE

        val projectFee = context.getString(R.string.fee_project) + ": " + house.agent_Money
        itemView.item_secondary_project_tv_fee.text = projectFee
        itemView.item_secondary_project_tv_fee.visibility = if (house.agent_Money.isEmpty())
            View.INVISIBLE else View.VISIBLE

        LHPicasso.loadImage(house.image, itemView.item_secondary_project_imgMedia)
    }

}
