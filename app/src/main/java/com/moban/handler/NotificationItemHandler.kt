package com.moban.handler

import com.moban.model.data.notification.Notification

/**
 * Created by LenVo on 5/11/18.
 */
interface NotificationItemHandler {
    fun onClicked(notification: Notification)
}
