package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.project.Project
import com.moban.util.Font
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_search_project.view.*

/**
 * Created by LenVo on 5/6/18.
 */
class SearchProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(project: Project) {
        val context = itemView.context

        val fontRegular = Font.regularTypeface(context)
        val fontMedium = Font.mediumTypeface(context)

        itemView.item_search_project_tv_project_name.typeface = fontMedium

        arrayOf(itemView.item_search_project_tv_project_address,
                itemView.item_search_project_tv_project_price)
                .forEach {
                    it.typeface = fontRegular
                }

        itemView.item_search_project_tv_project_name.text = project.product_Name
        itemView.item_search_project_tv_project_address.text = project.fullAddress()

        val price = project.priceString()
        if (price != "0") {
            val priceStr = price + context.getString(R.string.project_price_unit)
            itemView.item_search_project_tv_project_price.text = priceStr
        }

        LHPicasso.loadImage(project.product_Image, itemView.item_search_project_img_media)
    }

}
