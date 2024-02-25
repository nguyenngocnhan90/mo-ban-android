package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.HostItemHandler
import com.moban.view.viewholder.HostItemViewHolder

/**
 * Created by LenVo on 7/26/18.
 */
class HostAdapter : AbsAdapter<HostItemViewHolder>() {

    var listener: HostItemHandler? = null

    private var hosts: MutableList<String> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return HostItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hosts.count()
    }

    override fun updateView(holder: HostItemViewHolder, position: Int) {
        val block = hosts[position]
        if (block.isNullOrEmpty()) {
            return
        }

        holder.bind(block)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(block)
        }
    }

    fun setHostList(hosts: List<String>) {
        this.hosts.clear()
        this.hosts.addAll(hosts)
        notifyDataSetChanged()
    }
}
