package com.moban.notification

import com.google.gson.Gson
import com.moban.LHApplication
import com.moban.flow.main.MainActivity
import com.moban.helper.LocalStorage
import com.moban.model.data.notification.Notification
import com.moban.model.data.notification.SimpleNotification
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal

/**
 * Created by thangnguyen on 1/19/18.
 */
class NotificationOpenedHandler : OneSignal.OSNotificationOpenedHandler {

    override fun notificationOpened(osNotificationOpenResult: OSNotificationOpenedResult) {
        val dataString = osNotificationOpenResult.notification.additionalData
        val simpleNotification = if (dataString == null) {
            val notify = SimpleNotification()
            val notificationId = osNotificationOpenResult.notification.notificationId.toIntOrNull()
            notificationId?.let {
                notify.notification_id = it
            }
            notify
        } else {
            Gson().fromJson<SimpleNotification>(
                dataString.toString(),
                SimpleNotification::class.java
            )
        }

        simpleNotification?.let {
            LocalStorage.saveNotification(Notification.init(it))
            MainActivity.start(LHApplication.instance)
        }
    }
}
