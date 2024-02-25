package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_search_project.view.*

/**
 * Created by LenVo on 5/6/18.
 */
class SearchSecondaryProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(house: SecondaryHouse) {

        itemView.item_search_project_tv_project_name.text = house.contract_code
        itemView.item_search_project_tv_project_address.text = house.fullAddress()
        itemView.item_search_project_tv_project_price.text = house.price

        LHPicasso.loadImage(house.image, itemView.item_search_project_img_media)
    }

}
