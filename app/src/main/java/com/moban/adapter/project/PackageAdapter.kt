package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.PackageItemHandler
import com.moban.model.data.project.SalePackage
import com.moban.view.viewholder.PackageItemViewHolder
import kotlinx.android.synthetic.main.item_package.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class PackageAdapter : AbsAdapter<PackageItemViewHolder>() {

    var listener: PackageItemHandler? = null

    var data: MutableList<SalePackage> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_package
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PackageItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun updateView(holder: PackageItemViewHolder, position: Int) {
        val block = data[position]
        holder.bind(block)

        holder.itemView.setOnClickListener { _ ->
            listener?.onViewDetail(block)
        }

        holder.itemView.item_package_btn_join.setOnClickListener { _ ->
            listener?.onSubscribed(block)
        }
    }

    fun updateBlocks(blocks: List<SalePackage>) {
        this.data.clear()
        this.data = blocks.toMutableList()
        this.notifyDataSetChanged()
    }
}
