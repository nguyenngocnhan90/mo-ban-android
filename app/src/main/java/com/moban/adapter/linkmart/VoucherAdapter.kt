package com.moban.adapter.linkmart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.VoucherItemHandler
import com.moban.model.data.linkmart.Voucher
import com.moban.view.viewholder.VoucherItemViewHolder

class VoucherAdapter : AbsAdapter<VoucherItemViewHolder>() {
    var listener: VoucherItemHandler? = null
    var vouchers: MutableList<Voucher> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_voucher
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return VoucherItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vouchers.size
    }

    override fun updateView(holder: VoucherItemViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.bind(voucher)

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(voucher)
        }
    }
}
