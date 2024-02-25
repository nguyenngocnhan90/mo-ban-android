package com.moban.adapter.policy

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.DocumentItemHandler
import com.moban.model.data.document.DocumentGroup
import com.moban.view.viewholder.PolicyDocumentGroupItemViewHolder

/**
 * Created by LenVo on 7/19/18.
 */

class PolicyDocumentGroupAdapter : AbsAdapter<PolicyDocumentGroupItemViewHolder>() {

    var listener: DocumentItemHandler? = null

    private var documentGroups: MutableList<DocumentGroup> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_policy_group
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyDocumentGroupItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return PolicyDocumentGroupItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return documentGroups.count()
    }

    override fun updateView(holder: PolicyDocumentGroupItemViewHolder, position: Int) {
        val document = documentGroups[position]
        holder.bind(document)
    }

    fun setDocumentGroupList(documentGroupsList: List<DocumentGroup>) {
        this.documentGroups.clear()
        this.documentGroups.addAll(documentGroupsList)
        notifyDataSetChanged()
    }

}
