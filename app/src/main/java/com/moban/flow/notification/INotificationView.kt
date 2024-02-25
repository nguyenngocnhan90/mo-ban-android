package com.moban.flow.notification

import com.moban.model.data.notification.Notification
import com.moban.model.response.notification.ListNotification
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 5/11/18.
 */
interface INotificationView : BaseMvpView {
    fun bindingNotifications(notifications: ListNotification)
    fun markAsRead(notification: Notification)
}
