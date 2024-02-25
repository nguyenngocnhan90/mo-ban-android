package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.model.data.user.User
import com.moban.view.viewholder.TextItemViewHolder

class UserMenuAdapter : AbsAdapter<TextItemViewHolder>() {
    private var data: MutableList<User> = ArrayList()
    var listener: UserMenuItemHandler? = null
    interface UserMenuItemHandler {
        fun onClicked(item: User)
    }

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return TextItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun updateView(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item.name)

        holder.itemView.setOnClickListener {
            listener?.onClicked(item)
        }
    }

    fun setDataList(dataList: List<User>) {
        this.data.clear()
        this.data.addAll(dataList)
        notifyDataSetChanged()
    }
}
