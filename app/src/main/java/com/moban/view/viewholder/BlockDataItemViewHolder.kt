package com.moban.view.viewholder

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.adapter.project.ApartmentAdapter
import com.moban.handler.ApartmentItemHandler
import com.moban.model.data.project.Apartment
import com.moban.model.data.project.Floor
import kotlinx.android.synthetic.main.item_block_data.view.*

/**
 * Created by LenVo on 7/21/18.
 */
class BlockDataItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private var context: Context? = null
    private val apartmentAdapter: ApartmentAdapter = ApartmentAdapter()
    var listener: ApartmentItemHandler? = null

    fun bind(floor: Floor, listener: ApartmentItemHandler?) {
        this.context = itemView.context
        this.listener = listener
        val floorName = context?.getString(R.string.floor) + " " + floor.group
        itemView.item_block_data_tv_name.text = floorName
        val layoutManager = GridLayoutManager(context!!, 3)
        itemView.item_block_data_recycleView.layoutManager = layoutManager
        itemView.item_block_data_recycleView.adapter = apartmentAdapter
        apartmentAdapter.listener = object : ApartmentItemHandler {
            override fun onClicked(apartment: Apartment) {
                listener?.onClicked(apartment)
            }
        }
        apartmentAdapter.apartments.addAll(floor.items)
        apartmentAdapter.notifyDataSetChanged()
        itemView.item_block_data_recycleView.isNestedScrollingEnabled = false
    }
}
