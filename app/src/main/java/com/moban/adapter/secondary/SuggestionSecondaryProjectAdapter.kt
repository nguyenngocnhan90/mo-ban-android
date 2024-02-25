package com.moban.adapter.secondary

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.SecondaryHouseItemHandler
import com.moban.model.data.secondary.SecondaryHouse
import com.moban.view.viewholder.SuggestionSecondaryProjectItemViewHolder
import kotlinx.android.synthetic.main.item_suggestion_project.view.*

/**
 * Created by LenVo on 5/4/18.
 */

class SuggestionSecondaryProjectAdapter : AbsAdapter<SuggestionSecondaryProjectItemViewHolder>() {

    var listener: SecondaryHouseItemHandler? = null

    var houses: MutableList<SecondaryHouse> = ArrayList()

    val isDataEmpty: Boolean
        get() = houses.isEmpty()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_suggestion_project
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionSecondaryProjectItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return SuggestionSecondaryProjectItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return houses.count()
    }

    override fun updateView(holder: SuggestionSecondaryProjectItemViewHolder, position: Int) {
        val project = houses[position]
        holder.bind(project)

        val itemView = holder.itemView

        itemView.item_suggestion_img_media.setOnClickListener {
            listener?.onClicked(project)
        }
    }

    fun appendProjects(houses: List<SecondaryHouse>) {
        this.houses.addAll(houses.toMutableList())
        this.notifyDataSetChanged()
    }
}
