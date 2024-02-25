package com.moban.flow.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.flow.cointransfer.CoinTransferActivity
import com.moban.helper.LocalStorage
import com.moban.util.DialogUtil

class DialogMessageActivity : Activity() {
    private lateinit var message: String
    private var userId: Int = 0
    private var dialog: Dialog? = null

    companion object {
        private const val DIALOG_KEY_MESSAGE = "DIALOG_KEY_MESSAGE"
        private const val DIALOG_KEY_USER_ID = "DIALOG_KEY_USER_ID"

        fun start(context: Context, post: String, userId: Int) {
            val intent = Intent(context, DialogMessageActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(DIALOG_KEY_MESSAGE, post)
            bundle.putInt(DIALOG_KEY_USER_ID, userId)
            intent.putExtras(bundle)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val bundle = intent.extras
        if (intent.hasExtra(DIALOG_KEY_MESSAGE)) {
            message = bundle?.getSerializable(DIALOG_KEY_MESSAGE) as String
        }

        if (intent.hasExtra(DIALOG_KEY_USER_ID)) {
            userId = bundle?.getInt(DIALOG_KEY_USER_ID)!!
        }

        LocalStorage.user()?.let {
            if (userId > 0 && userId != it.id) {
                return
            }
        }

        showDialog()
    }

    private fun showDialog() {
        dialog = DialogUtil.showConfirmDialog(this, false, getString(R.string.congrats), message,
                getString(R.string.resend_lixi), getString(R.string.no),
                View.OnClickListener {
                    dialog?.dismiss()
                    CoinTransferActivity.start(this)
                },
                View.OnClickListener {
                    dialog?.dismiss()
                    this@DialogMessageActivity.finish()
                })
    }


}
