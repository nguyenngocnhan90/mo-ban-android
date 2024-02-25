package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.ApartmentItemHandler
import com.moban.model.data.project.Floor
import com.moban.view.viewholder.BlockDataItemViewHolder

/**
 * Created by LenVo on 7/21/18.
 */
class BlockDataAdapter: AbsAdapter<BlockDataItemViewHolder>() {

    private var floors: MutableList<Floor> = ArrayList()
    var listener: ApartmentItemHandler? = null

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_block_data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockDataItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return BlockDataItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return floors.count()
    }

    override fun updateView(holder: BlockDataItemViewHolder, position: Int) {
        val floor = floors[position]
        holder.bind(floor, listener)
    }

    fun updateFloors(blocks: List<Floor>) {
        this.floors.clear()
        this.floors = blocks.toMutableList()
        this.notifyDataSetChanged()
    }
}
