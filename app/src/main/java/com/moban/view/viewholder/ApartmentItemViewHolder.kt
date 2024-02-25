package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.project.Apartment
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_apartment.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class ApartmentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(apartment: Apartment) {
        val context = itemView.context
        itemView.item_apartment_name.text = apartment.flat_Name
        itemView.item_apartment_size.text = apartment.flat_Area
        val status = apartment.flat_Status
        when {
            status == 0 -> {
                itemView.setBackgroundColor(Util.getColor(context, R.color.color_cart_status_empty))
            }
            status == 1 -> {
                itemView.setBackgroundColor(Util.getColor(context, R.color.color_cart_status_book))
            }
            status == 2 -> {
                itemView.setBackgroundColor(Util.getColor(context, R.color.color_cart_status_booked))
            }
            status == 3 -> {
                itemView.setBackgroundColor(Util.getColor(context, R.color.color_cart_status_booked))
            }
            status == 4 -> {
                itemView.setBackgroundColor(Util.getColor(context, R.color.color_cart_status_contract))
            }
        }
    }

}
