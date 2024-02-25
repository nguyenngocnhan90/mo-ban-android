package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.FloorItemHandler
import com.moban.model.data.project.Floor
import com.moban.view.viewholder.FloorItemViewHolder
import kotlinx.android.synthetic.main.item_dialog.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class FloorAdapter : AbsAdapter<FloorItemViewHolder>() {

    var listener: FloorItemHandler? = null

    private var floors: MutableList<Floor> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return FloorItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return floors.count()
    }

    override fun updateView(holder: FloorItemViewHolder, position: Int) {
        val block = floors[position]
        holder.bind(block)

        holder.itemView.item_dialog_text.setOnClickListener { _ ->
            listener?.onClicked(block, position)
        }
    }

    fun setFloorsList(floors: List<Floor>) {
        this.floors.clear()
        this.floors.addAll(floors)
        notifyDataSetChanged()
    }
}
