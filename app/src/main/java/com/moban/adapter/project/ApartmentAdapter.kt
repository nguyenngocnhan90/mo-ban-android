package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.ApartmentItemHandler
import com.moban.model.data.project.Apartment
import com.moban.view.viewholder.ApartmentItemViewHolder
import kotlinx.android.synthetic.main.item_apartment.view.*

/**
 * Created by LenVo on 3/5/18.
 */

class ApartmentAdapter : AbsAdapter<ApartmentItemViewHolder>() {

    var listener: ApartmentItemHandler? = null

    var apartments: MutableList<Apartment> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_apartment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApartmentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return ApartmentItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apartments.count()
    }

    override fun updateView(holder: ApartmentItemViewHolder, position: Int) {
        val apartment = apartments[position]
        holder.bind(apartment)

        holder.itemView.item_apartment_comtent.setOnClickListener { _ ->
            listener?.onClicked(apartment)
        }
    }

    fun setApartmentsList(apartments: List<Apartment>) {
        this.apartments.clear()
        this.apartments.addAll(apartments)
        notifyDataSetChanged()
    }

}
