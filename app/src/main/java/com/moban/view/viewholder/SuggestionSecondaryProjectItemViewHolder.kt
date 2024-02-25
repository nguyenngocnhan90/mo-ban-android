package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_suggestion_project.view.*

/**
 * Created by LenVo on 3/11/18.
 */
class SuggestionSecondaryProjectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(house: SecondaryHouse) {
        itemView.item_suggestion_tv_project_name.text = house.contract_code

        LHPicasso.loadImage(house.image, itemView.item_suggestion_img_media)
    }

}
