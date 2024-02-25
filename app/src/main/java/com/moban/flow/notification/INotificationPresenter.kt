package com.moban.flow.notification

import com.moban.model.data.notification.Notification
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 5/11/18.
 */
interface INotificationPresenter : BaseMvpPresenter<INotificationView> {
    fun loadNotifications(page: Int)
    fun markAsRead(notification: Notification)
}
