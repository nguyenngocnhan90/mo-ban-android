package com.moban.flow.pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moban.R
import kotlinx.android.synthetic.main.activity_pdf.*
import kotlinx.android.synthetic.main.nav.view.*
import java.io.File


class PdfActivity : AppCompatActivity() {
    private var title: String = ""
    private var link: String = ""

    companion object {
        const val BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE"
        const val BUNDLE_KEY_LINK = "BUNDLE_KEY_LINK"

        fun start(context: Context, title: String, link: String) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_TITLE, title)
            bundle.putSerializable(BUNDLE_KEY_LINK, link)

            val intent = Intent(context, PdfActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_TITLE)) {
            title = bundle?.getSerializable(BUNDLE_KEY_TITLE) as String

            pdf_nav.nav_tvTitle.text = title
        }

        if (intent.hasExtra(BUNDLE_KEY_LINK)) {
            link = bundle?.getSerializable(BUNDLE_KEY_LINK) as String

            if (link.contains("http://") || link.contains("https://")) {
                val uri = Uri.parse(link)
                pdf_viewer.fromUri(uri).enableSwipe(true).load()
            } else {
                val file = File(link)
                pdf_viewer.fromFile(file).enableSwipe(true).load()
            }
        }

        pdf_nav.nav_imgBack.setOnClickListener {
            finish()
        }
    }
}
