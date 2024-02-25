package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.DocumentItemHandler
import com.moban.model.data.document.Document
import com.moban.view.viewholder.DocumentItemViewHolder

/**
 * Created by LenVo on 3/25/18.
 */

class DocumentAdapter : AbsAdapter<DocumentItemViewHolder>() {

    var listener: DocumentItemHandler? = null

    private var documents: MutableList<Document> = ArrayList()

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_document
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return DocumentItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return documents.count()
    }

    override fun updateView(holder: DocumentItemViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document)
    }

    fun setDocumentList(documentsList: List<Document>) {
        this.documents.clear()
        this.documents.addAll(documentsList)
        notifyDataSetChanged()
    }
}
