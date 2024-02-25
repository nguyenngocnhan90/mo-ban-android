package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.address.City
import kotlinx.android.synthetic.main.item_dialog.view.*

/**
 * Created by LenVo on 7/26/18.
 */
class CityItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(city: City) {
        val context = itemView.context
        itemView.item_dialog_text.text = city.city_Name
    }

}
