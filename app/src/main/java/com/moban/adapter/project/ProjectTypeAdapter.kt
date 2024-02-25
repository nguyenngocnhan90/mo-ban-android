package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.ProjectTypeItemHandler
import com.moban.model.data.statistic.Statistic
import com.moban.util.Device
import com.moban.view.viewholder.ProjectTypeItemViewHolder
import kotlin.math.max

/**
 * Created by LenVo on 7/17/18.
 */
class ProjectTypeAdapter(private val type: Int = PROJECT_TYPE) : AbsAdapter<ProjectTypeItemViewHolder>() {
    companion object {
        const val PROJECT_TYPE: Int = 0
        const val PROJECT_TYPE_HOME: Int = 1
    }
    var statistics: MutableList<Statistic> = ArrayList()
    var listener: ProjectTypeItemHandler? = null
    var selectedStatistic = Statistic()

    override fun getLayoutResourceId(viewType: Int): Int {
        return if (type == PROJECT_TYPE) R.layout.item_project_type else R.layout.item_statistic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectTypeItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return ProjectTypeItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return statistics.count()
    }

    override fun updateView(holder: ProjectTypeItemViewHolder, position: Int) {
        val statistic = statistics[position]
        if (type == PROJECT_TYPE) {
            val minWidth = Device.getScreenWidth()/5
            val widthItem = Device.getScreenWidth()/statistics.count()
            holder.bind(statistic, max(minWidth, widthItem), selectedStatistic)
        } else {
            holder.bind(statistic)
        }

        holder.itemView.setOnClickListener {
            listener?.onSelected(statistic)
        }
    }

    fun setStatisticsList(statistics: List<Statistic>) {
        this.statistics = statistics.toMutableList()
        notifyDataSetChanged()
    }

    fun selectStatistic(statistic: Statistic) {
        selectedStatistic = statistic
        notifyDataSetChanged()
    }

}
