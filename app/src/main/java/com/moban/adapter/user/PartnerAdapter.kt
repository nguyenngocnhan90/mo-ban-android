package com.moban.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.PartnerItemHandler
import com.moban.model.data.user.User
import com.moban.view.viewholder.PartnerItemViewHolder

/**
 * Created by LenVo on 5/11/18.
 */
class PartnerAdapter:  AbsAdapter<PartnerItemViewHolder>() {

    var listener: PartnerItemHandler? = null

    private var users: MutableList<User> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_partner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PartnerItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.count()
    }

    override fun updateView(holder: PartnerItemViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(user)
        }
    }

    fun setUsers(users: List<User>) {
        this.users = users.toMutableList()
        notifyDataSetChanged()
    }

    fun removeAllUsers() {
        this.users.clear()
        notifyDataSetChanged()
    }

}
