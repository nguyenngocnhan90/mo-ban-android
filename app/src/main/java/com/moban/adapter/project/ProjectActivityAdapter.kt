package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.PackageItemHandler
import com.moban.model.data.project.ProjectActivity
import com.moban.view.viewholder.ProjectActivityItemViewHolder

/**
 * Created by LenVo on 3/17/18.
 */
class ProjectActivityAdapter : AbsAdapter<ProjectActivityItemViewHolder>() {

    var listener: PackageItemHandler? = null

    var data: MutableList<ProjectActivity> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectActivityItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return ProjectActivityItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun updateView(holder: ProjectActivityItemViewHolder, position: Int) {
        val block = data[position]
        holder.bind(block)

    }

    fun updateData(blocks: List<ProjectActivity>) {
        this.data.clear()
        this.data = blocks.toMutableList()
        this.notifyDataSetChanged()
    }
}
