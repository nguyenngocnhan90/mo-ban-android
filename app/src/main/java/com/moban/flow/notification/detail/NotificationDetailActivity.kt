package com.moban.flow.notification.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import com.moban.R
import com.moban.enum.GACategory
import com.moban.model.data.notification.Notification
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import kotlinx.android.synthetic.main.activity_notification_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class NotificationDetailActivity : BaseMvpActivity<INotificationDetailView, INotificationPresenter>(), INotificationDetailView {
    override var presenter: INotificationPresenter = NotificationDetailPresenterIml()
    private lateinit var notification: Notification

    override fun getLayoutId(): Int {
        return R.layout.activity_notification_detail
    }

    companion object {
        private const val BUNDLE_KEY_NOTIFICATION = "BUNDLE_KEY_NOTIFICATION"
        private const val BUNDLE_KEY_NOTIFICATION_ID = "BUNDLE_KEY_NOTIFICATION_ID"

        fun start(context: Context, notification: Notification) {
            val intent = Intent(context, NotificationDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_NOTIFICATION, notification)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, notificationId: Int) {
            val intent = Intent(context, NotificationDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_NOTIFICATION_ID, notificationId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        notification_detail_nav.nav_tvTitle.text = getString(R.string.notification)
        notification_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        var id = 0
        if (intent.hasExtra(BUNDLE_KEY_NOTIFICATION)) {
            val notification = bundle?.getSerializable(BUNDLE_KEY_NOTIFICATION) as Notification
            bindingNotification(notification)
            id = notification.id
        } else if (intent.hasExtra(BUNDLE_KEY_NOTIFICATION_ID)) {
            val notificationId = bundle?.getSerializable(BUNDLE_KEY_NOTIFICATION_ID) as Int
            presenter.loadNotification(notificationId)
            id = notificationId
        }

        setGAScreenName("Notification Detail $id", GACategory.ACCOUNT)
    }

    override fun bindingNotification(notification: Notification) {
        this.notification = notification
        val isPlainNotification = notification.html_Body.isNullOrEmpty()

        notification_detail_webview.visibility = if (isPlainNotification) View.GONE else View.VISIBLE
        notification_detail_content_text.visibility = if (isPlainNotification) View.VISIBLE else View.GONE

        if (isPlainNotification) {
            fillPlainNotification()
        } else {
            fillHtmlNotification()
        }
    }

    override fun error(message: String) {
        DialogUtil.showErrorDialog(this, "Lỗi", message, "OK", null)
    }

    private fun fillPlainNotification() {
        notification_detail_tv_title.text = notification.title
        notification_detail_tv_body.text = notification.body
    }

    private fun fillHtmlNotification() {
        notification_detail_webview.webViewClient = WebViewClient()
        notification_detail_webview.settings.javaScriptEnabled = true
        notification_detail_webview.loadData(notification.html_Body ?: "", "text/html; charset=UTF-8", null)
    }
}
