package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.MissionItemHandler
import com.moban.model.data.user.Mission
import com.moban.view.viewholder.MissionItemViewHolder

class MissionAdapter : AbsAdapter<MissionItemViewHolder>() {

    var missions: List<Mission> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: MissionItemHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType),
                parent,
                false)
        return MissionItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return missions.count()
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_mission
    }

    override fun updateView(holder: MissionItemViewHolder, position: Int) {
        if (position < itemCount) {
            val mission = missions[position]
            
            holder.bind(mission)
            holder.itemView.setOnClickListener {
               listener?.onClicked(mission)
            }
        }
    }
}
