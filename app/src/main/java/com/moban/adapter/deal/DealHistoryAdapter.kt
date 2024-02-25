package com.moban.adapter.deal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.moban.R
import com.moban.model.data.deal.Deal
import com.moban.model.data.deal.DealHistory
import com.moban.util.DateUtil
import kotlinx.android.synthetic.main.item_deal_history.view.*

class DealHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var deal: Deal? = null
    var histories: List<DealHistory> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_deal_history, parent, false)
        return DealHistoryItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return histories.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        deal?.let {
            if (position < histories.size) {
                (holder as DealHistoryItemViewHolder).bind(histories[position], it)
            }
        }
    }

    class DealHistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(history: DealHistory, deal: Deal) {
            itemView.deal_status.text = history.deal_Status_Name

            var date = ""
            if (history.is_Finished) {
                date = "Ngày " + DateUtil.dateStringFromSeconds(history.deal_Date.toLong(), "dd/MM/yyyy")
            }
            itemView.deal_status_date.text = date

            val finishedStatuses = deal.finishedHistories()
            val isFinished = finishedStatuses.map { it.deal_Status }.contains(history.deal_Status)
            var isCurrentStep: Boolean = false

            if (finishedStatuses.isNotEmpty()) {
                finishedStatuses.last().let {
                    isCurrentStep = it.deal_Status == history.deal_Status
                }
            }

            itemView.deal_item_tv_current_state.visibility = if (isCurrentStep)
                View.VISIBLE else View.INVISIBLE

            itemView.deal_item_check.visibility = if (isCurrentStep)
                View.VISIBLE else View.INVISIBLE

            itemView.deal_status_none.visibility = if (isCurrentStep)
                View.INVISIBLE else View.VISIBLE
            itemView.deal_status_none.background = ContextCompat.getDrawable(
                    itemView.context,
                    if (isFinished) R.drawable.circle_icon_accent else R.drawable.circle_icon_cancel)
        }
    }
}