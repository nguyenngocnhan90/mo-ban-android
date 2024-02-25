package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.model.data.address.District
import kotlinx.android.synthetic.main.item_dialog.view.*

/**
 * Created by LenVo on 7/26/18.
 */
class DistrictItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(district: District) {
        val context = itemView.context
        itemView.item_dialog_text.text = district.district_Name
    }

}
