package com.moban.view.viewholder

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.moban.LHApplication
import com.moban.R
import com.moban.flow.pdf.PdfActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.DocumentBottomMenuItemHandler
import com.moban.model.data.document.Document
import com.moban.util.DialogBottomSheetUtil
import com.moban.util.PdfUtil
import com.moban.util.Permission
import kotlinx.android.synthetic.main.item_document.view.*

/**
 * Created by LenVo on 3/17/18.
 */
class DocumentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(document: Document) {
        val context = itemView.context
        val fontBold = Typeface.createFromAsset(context.assets,
                context.resources.getString(R.string.font_bold))
        itemView.item_document_name.typeface = fontBold
        itemView.item_document_name.text = document.doc_Name
        val dateUpdate = context.getString(R.string.date_update) + " " + document.getDocumentUpdatedDate()
        itemView.item_document_update_date.text = dateUpdate

        itemView.item_document_btn_more.setOnClickListener {
            DialogBottomSheetUtil.showDialogDocumentMenu(context, PdfUtil.shouldShowDownloadBtn(document.link),
                    object: DocumentBottomMenuItemHandler {
                        override fun onDownload() {
                            if(Permission.checkPermissionWriteExternalStorage(context) &&
                                    Permission.checkPermissionReadExternalStorage(context)) {
                                PdfUtil.downloadFile(document.link, itemView.context)
                            }
                        }

                        override fun onShare() {
                            PdfUtil.openShareLink(context, document.link)
                        }
                    })
        }

        itemView.setOnClickListener {
            val filePath = LHApplication.commonStorage.getString(document.link)
            if (filePath.isNullOrEmpty()) {
                WebViewActivity.start(context, document.doc_Name, PdfUtil.generatePdfDocURL(document.link))
            } else {
                PdfActivity.start(context, document.doc_Name, filePath)
            }
        }

        itemView.setOnLongClickListener {
            PdfUtil.openShareLink(context, document.link)
            true
        }
    }



}
