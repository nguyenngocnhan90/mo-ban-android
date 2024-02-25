package com.moban.adapter.address

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.DistrictItemHandler
import com.moban.model.data.address.District
import com.moban.view.viewholder.DistrictItemViewHolder

/**
 * Created by LenVo on 7/26/18.
 */
class DistrictAdapter : AbsAdapter<DistrictItemViewHolder>() {

    var listener: DistrictItemHandler? = null

    private var districts: MutableList<District> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return DistrictItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return districts.count()
    }

    override fun updateView(holder: DistrictItemViewHolder, position: Int) {
        if (position < districts.size) {
            val block = districts[position]
            holder.bind(block)

            holder.itemView.setOnClickListener { view ->
                listener?.onClicked(block)
            }
        }
    }

    fun setDistrictsList(districts: List<District>) {
        this.districts.clear()
        this.districts.addAll(districts)
        notifyDataSetChanged()
    }
}
