package com.moban.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.moban.LHApplication
import com.moban.R
import com.moban.network.service.FileService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

/**
 * Created by LenVo on 4/13/18.
 */
class PdfUtil {
    companion object {

        private const val GOOGLE_DOC_EMBEDDED_URL = "https://docs.google.com/gview?embedded=true&url="
        private const val GOOGLE_DOMAIN_URL = "google.com"

        fun generatePdfDocURL(url: String) : String {
            if (url.contains(".pdf") || url.contains(".doc") || url.contains(".docx") ||
                    url.contains(".xlsx")|| url.contains(".xls") || url.contains(".ppt")
                    || url.contains(".pptx")) {
                return GOOGLE_DOC_EMBEDDED_URL + url
            }

            return url
        }

        fun downloadFile(link: String, context: Context) {
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

        fun shouldShowDownloadBtn(link: String): Boolean {
            val ext = File(link).extension
            if ("pdf" != ext.toLowerCase()) {
                return false
            }
            return LHApplication.commonStorage.getString(link).isNullOrEmpty()
        }

        fun isDownloaded(link: String): Boolean {
            val ext = File(link).extension
            return "pdf" == ext.toLowerCase() && !LHApplication.commonStorage.getString(link).isNullOrEmpty()
        }

        fun openShareLink(context: Context, link: String) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    link)
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_kit)))
        }
    }
}
