package com.moban.view.viewholder

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.policy.PolicyDocumentAdapter
import com.moban.enum.DocumentObjectType
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.pdf.PdfActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.model.data.document.Document
import com.moban.network.service.FileService
import com.moban.util.*
import kotlinx.android.synthetic.main.item_policy.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

/**
 * Created by LenVo on 3/17/18.
 */
class PolicyDocumentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(document: Document, isSpecialDeal: Boolean, isFlashSaleDeal: Boolean, type: Int = PolicyDocumentAdapter.HORIZONTAL, isProjectPolicy: Boolean = false) {
        val context = itemView.context
        LHPicasso.loadImage(document.image, itemView.item_policy_img)

        itemView.item_policy_tv.text = document.doc_Name

        val discountFee = document.finalDiscountServiceFee()
        val fee = document.finalServiceFee()
        itemView.item_policy_tv_service_fee.text = fee
        itemView.item_policy_tv_discount_service_fee.text = discountFee
        itemView.item_policy_tv_service_fee.paintFlags = itemView.item_policy_tv_service_fee.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        val titleColor = Util.getColor(context, if (isSpecialDeal || isProjectPolicy) R.color.white else R.color.black)
        itemView.item_policy_tv.setTextColor(titleColor)
        itemView.item_policy_tv_discount_service_fee.setTextColor(Util.getColor(context, if (isSpecialDeal) R.color.white else R.color.colorAccent))
        itemView.item_policy_tv_service_fee.setTextColor(Util.getColor(context, if (isSpecialDeal) R.color.color_white_60 else R.color.color_black_60))

        itemView.item_policy_sale_progress.visibility = if (isFlashSaleDeal) View.VISIBLE else View.GONE
        itemView.item_policy_sale_progress.setCurrentWithAnimation(document.sold_Count, 0, document.stocking_Count)

        itemView.setOnClickListener {
            val documentType = DocumentObjectType.from(document.object_Type)
            if (documentType == null) {
                viewDocument(context, document)
            }
            documentType?.let { type ->
                when (type) {
                    DocumentObjectType.post -> {
                        PostDetailActivity.start(context, document.object_Id)
                    }
                    DocumentObjectType.project -> {
                        ProjectDetailActivity.start(context, document.object_Id)
                    }
                    else  -> {
                        viewDocument(context, document)
                    }
                }
            }
        }

        if (type == PolicyDocumentAdapter.VERTICAL) {
            val param = itemView.layoutParams
            val width = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(itemView.context, 40)) / 2
            param.height = width + BitmapUtil.convertDpToPx(itemView.context, 80)
            param.width = width
            itemView.layoutParams = param
        }

        if (isProjectPolicy) {
            val param = itemView.layoutParams
            val heightView = BitmapUtil.convertDpToPx(itemView.context, 260)
            param.height = heightView
            itemView.layoutParams = param
        }
    }

    private fun viewDocument(context: Context, document: Document) {
        val filePath = LHApplication.commonStorage.getString(document.link)
        if (filePath.isNullOrEmpty()) {
            WebViewActivity.start(context, document.doc_Name, PdfUtil.generatePdfDocURL(document.link))
        } else {
            PdfActivity.start(context, document.doc_Name, filePath)
        }
    }

    private fun downloadFile(link: String, itemView: View) {
        val context = itemView.context
        val fileName = File(link).name
        val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
        val fileService: FileService? = retrofit?.create(FileService::class.java)
        fileService?.downloadFile(link)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.let { it ->
                    val path = FileUtil.writeResponseBodyToDisk(it, fileName)
                    path?.let {
                        LHApplication.commonStorage.putString(link, it)
                        Toast.makeText(context, context.getString(R.string.downloaded_document), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun shouldShowDownloadBtn(link: String): Boolean {
        val ext = File(link).extension
        if ("pdf" != ext.toLowerCase()) {
            return false
        }
        return LHApplication.commonStorage.getString(link).isNullOrEmpty()
    }

    private fun isDownloaded(link: String): Boolean {
        val ext = File(link).extension
        return "pdf" == ext.toLowerCase() && !LHApplication.commonStorage.getString(link).isNullOrEmpty()
    }

    private fun openShareLink(context: Context, link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                link)
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_kit)))
    }

}
