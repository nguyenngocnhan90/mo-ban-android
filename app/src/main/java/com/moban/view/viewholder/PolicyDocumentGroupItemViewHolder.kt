package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.adapter.policy.PolicyDocumentAdapter
import com.moban.model.data.document.DocumentGroup
import kotlinx.android.synthetic.main.item_policy_group.view.*

/**
 * Created by LenVo on 7/19/18.
 */
class PolicyDocumentGroupItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val documentAdapter: PolicyDocumentAdapter = PolicyDocumentAdapter()

    fun bind(documentGroup: DocumentGroup) {
        val context = itemView.context
        val title = "Chính sách theo dự án " + documentGroup.group
        itemView.item_policy_group_tv_title.text = title
        var layoutManager = GridLayoutManager(context, 2,LinearLayoutManager.HORIZONTAL, false)
        if (documentGroup.items.size <= 2) {
            layoutManager = GridLayoutManager(context, 1,LinearLayoutManager.HORIZONTAL, false)
        }
        itemView.item_policy_group_recycle_view.layoutManager = layoutManager
        itemView.item_policy_group_recycle_view.adapter = documentAdapter
        documentAdapter.setDocumentList(documentGroup.items.toMutableList())
    }

}
