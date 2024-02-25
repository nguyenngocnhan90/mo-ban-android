package com.moban.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.adapter.project.DocumentAdapter
import com.moban.model.data.document.DocumentGroup
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.item_document_group.view.*

/**
 * Created by LenVo on 7/19/18.
 */
class DocumentGroupItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val documentAdapter: DocumentAdapter = DocumentAdapter()

    fun bind(documentGroup: DocumentGroup) {
        val context = itemView.context
        itemView.item_document_group_tv_name.text = documentGroup.group
        val layoutManager = CustomLinearLayoutManager(context)
        itemView.item_document_group_recycleView.layoutManager = layoutManager
        itemView.item_document_group_recycleView.adapter = documentAdapter
        documentAdapter.setDocumentList(documentGroup.items.toMutableList())
    }

}
