package com.moban.flow.notification

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.data.notification.Notification
import com.moban.model.response.SimpleResponse
import com.moban.model.response.notification.ListNotificationResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.NotificationService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 5/11/18.
 */
class NotificationPresenterIml : BaseMvpPresenter<INotificationView>, INotificationPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val notificationService = retrofit?.create(NotificationService::class.java)

    private var noNetworkDialog: Dialog? = null
    private var view: INotificationView? = null
    private var context: Context? = null

    override fun attachView(view: INotificationView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadNotifications(page: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                loadNotifications(page)
            })
            return
        }

        notificationService?.notifications(page)?.enqueue(object : Callback<ListNotificationResponse> {
            override fun onFailure(call: Call<ListNotificationResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListNotificationResponse>?, response: Response<ListNotificationResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let {
                            view?.bindingNotifications(it)
                        }
                    }
                }
            }
        })
    }

    override fun markAsRead(notification: Notification) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        notificationService?.markAsRead(notification.id)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        view?.markAsRead(notification)
                    }
                }
            }
        })
    }

    /**
     * Network dialog
     */

    private fun dismissNetworkDialog() {
        noNetworkDialog?.dismiss()
    }
}
