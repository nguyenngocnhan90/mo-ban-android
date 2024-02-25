package com.moban.adapter.address

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.CityItemHandler
import com.moban.model.data.address.City
import com.moban.view.viewholder.CityItemViewHolder

/**
 * Created by LenVo on 7/26/18.
 */
class CityAdapter: AbsAdapter<CityItemViewHolder>() {

    var listener: CityItemHandler? = null

    private var cities: MutableList<City> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return CityItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.count()
    }

    override fun updateView(holder: CityItemViewHolder, position: Int) {
        if (position < cities.size) {
            val block = cities[position]
            holder.bind(block)

            holder.itemView.setOnClickListener { _ ->
                listener?.onClicked(block)
            }
        }
    }

    fun setCitiesList(cities: List<City>) {
        this.cities.clear()
        this.cities.addAll(cities)
        notifyDataSetChanged()
    }

}
