package com.moban.view.viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.project.SalePackage
import com.moban.util.BitmapUtil
import com.moban.util.Device
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_package.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class PackageItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    private var context: Context? = null

    fun bind(salePackage: SalePackage) {
        this.context = itemView.context
        itemView.item_package_tv_name.text = salePackage.subscriber_Name
        itemView.item_package_tv_commision.text = salePackage.subscriber_Content
        itemView.item_package_tv_desc.text = salePackage.subscriber_Content_Label

        val isHot = salePackage.subscriber_Type == "Hot"
        if (isHot) {
            itemView.item_package_img_hot.visibility = View.VISIBLE
        }
        val textColor = if (isHot) Util.getColor(context!!, R.color.white) else
            Util.getColor(context!!, R.color.color_black)
        itemView.item_package_btn_join.setTextColor(textColor)
        itemView.item_package_btn_join.background = context!!.getDrawable(if (isHot)
            R.drawable.background_yellow_round_5 else R.drawable.background_gray_round_5)

        val param = itemView.layoutParams
        val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(itemView.context, 60)) / 2
        param.height = heightView
        param.width = heightView
        itemView.layoutParams = param
    }
}
