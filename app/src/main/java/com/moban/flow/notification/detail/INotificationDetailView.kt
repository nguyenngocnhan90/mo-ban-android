package com.moban.flow.notification.detail

import com.moban.model.data.notification.Notification
import com.moban.mvp.BaseMvpView

interface INotificationDetailView: BaseMvpView {
    fun bindingNotification(notification: Notification)
    fun error(message: String)
}
