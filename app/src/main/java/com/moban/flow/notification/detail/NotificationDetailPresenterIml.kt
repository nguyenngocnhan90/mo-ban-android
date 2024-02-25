package com.moban.flow.notification.detail

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.notification.NotificationResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.NotificationService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NotificationDetailPresenterIml: BaseMvpPresenter<INotificationDetailView>, INotificationPresenter {
    private var view: INotificationDetailView? = null
    private var context: Context? = null

    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val notificationService = retrofit?.create(NotificationService::class.java)

    override fun attachView(view: INotificationDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadNotification(id: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        notificationService?.notification(id)?.enqueue(object : Callback<NotificationResponse> {
            override fun onFailure(call: Call<NotificationResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<NotificationResponse>?, response: Response<NotificationResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { notification ->
                            view?.bindingNotification(notification)
                        }
                    }
                    else {
                        it.error?.let { error ->
                            view?.error(error)
                        }
                    }
                }
            }
        })
    }
}
