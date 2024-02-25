package com.moban.flow.empty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moban.R
import kotlinx.android.synthetic.main.activity_empty_view.*
import kotlinx.android.synthetic.main.nav.view.*


class EmptyViewActivity : AppCompatActivity() {
    private var title: String = ""

    companion object {
        const val BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE"

        fun start(context: Context, title: String) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_TITLE, title)

            val intent = Intent(context, EmptyViewActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_view)

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_TITLE)) {
            title = bundle?.getSerializable(BUNDLE_KEY_TITLE) as String

            empty_view_nav.nav_tvTitle.text = title
        }

        empty_view_nav.nav_imgBack.setOnClickListener {
            finish()
        }
    }
}
