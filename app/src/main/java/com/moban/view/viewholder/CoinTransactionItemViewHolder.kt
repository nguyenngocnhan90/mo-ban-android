package com.moban.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.user.LhcTransaction
import com.moban.model.data.user.LinkPointTransaction
import kotlinx.android.synthetic.main.item_coin_transaction.view.*

class CoinTransactionItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(transaction: LhcTransaction) {
        val context = itemView.context

        itemView.item_transaction_name.text = transaction.target_Type
        itemView.item_transaction_detail.text = transaction.description

        val coin = transaction.linkCoin.toString() + " " + context.getString(R.string.lhc)
        itemView.item_transaction_changed_coin.text = coin

        val background = if (transaction.linkCoin > 0) R.drawable.background_button_linkhouse_round_4 else
            R.drawable.background_red_button_linkhouse_round_4

        itemView.item_transaction_changed_coin.background = context.getDrawable(background)
    }

    fun bind(transaction: LinkPointTransaction) {
        val context = itemView.context

        itemView.item_transaction_name.text = transaction.target_Type
        itemView.item_transaction_detail.text = transaction.description

        val coin = transaction.linkPoint.toString() + " " + context.getString(R.string.lhc)
        itemView.item_transaction_changed_coin.text = coin

        val background = if (transaction.linkPoint > 0) R.drawable.background_button_linkhouse_round_4 else
            R.drawable.background_red_button_linkhouse_round_4

        itemView.item_transaction_changed_coin.background = context.getDrawable(background)
    }
}
