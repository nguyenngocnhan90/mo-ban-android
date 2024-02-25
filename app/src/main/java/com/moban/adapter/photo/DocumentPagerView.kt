package com.moban.adapter.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.moban.LHApplication
import com.moban.R
import com.moban.enum.DocumentObjectType
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.flow.pdf.PdfActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.model.data.document.Document
import com.moban.util.LHPicasso
import com.moban.util.PdfUtil
import kotlinx.android.synthetic.main.item_photo_full_width.view.*

/**
 * Created by lenvo on 4/26/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class DocumentPagerView(context: Context) : PagerAdapter() {
    private var context: Context? = context
    private var documents: MutableList<Document> = ArrayList()

    override fun getCount(): Int {
        return documents.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_photo_full_width, null)
        val document = documents[position]
        LHPicasso.loadImage(document.image, view.item_photo_img)
        val context = view.context
        view.setOnClickListener {
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

        val viewPager: ViewPager = container as ViewPager
        viewPager.addView(view)
        return view
    }

    private fun viewDocument(context: Context, document: Document) {
        val filePath = LHApplication.commonStorage.getString(document.link)
        if (filePath.isNullOrEmpty()) {
            WebViewActivity.start(context, document.doc_Name, PdfUtil.generatePdfDocURL(document.link))
        } else {
            PdfActivity.start(context, document.doc_Name, filePath)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager: ViewPager = container as ViewPager
        val view: View = `object` as View
        viewPager.removeView(view)
    }

    fun setData(photos: List<Document>) {
        this.documents.clear()
        this.documents.addAll(photos)
        notifyDataSetChanged()
    }
}
