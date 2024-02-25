package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.R
import com.moban.model.data.user.LhcTransaction
import com.moban.util.Font
import kotlinx.android.synthetic.main.item_reward_transaction.view.*

/**
 * Created by LenVo on 3/29/18.
 */
class TransactionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(transaction: LhcTransaction) {
        val context = itemView.context

        itemView.item_reward_transaction_tv_coin.typeface = Font.mediumTypeface(context)
        arrayOf(itemView.item_reward_transaction_type, itemView.item_reward_transaction_description)
                .forEach {
                    it.typeface = Font.regularTypeface(context)
                }

        itemView.item_reward_transaction_type.text = transaction.target_Type
        itemView.item_reward_transaction_description.text = transaction.description
        itemView.item_reward_transaction_tv_coin.text = transaction.linkCoin.toString() + " " +
                context.getString(R.string.lhc)

        val color = if (transaction.linkCoin > 0) context.getDrawable(R.drawable.background_button_linkhouse_round_4)
                        else context.getDrawable(R.drawable.background_red_button_linkhouse_round_4)
        itemView.item_reward_transaction_tv_coin.background = color
    }
}
