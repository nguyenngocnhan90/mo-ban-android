package com.moban.model.data.notification

import com.google.gson.annotations.SerializedName
import com.moban.enum.NotificationStatus
import com.moban.model.BaseModel

/**
 * Created by neal on 3/21/18.
 */
class Notification : BaseModel() {
    var id: Int = 0
    var title: String = ""
    var body: String = ""

    @SerializedName("html_Body")
    var html_Body: String? = null

    @SerializedName("is_Read")
    var is_Read: Int = 0

    @SerializedName("linked_Object_Id")
    var linked_Object_Id: Int = 0

    var type: String = ""

    @SerializedName("created_Date")
    var created_Date: Double = 0.0

    companion object {
        fun init(simpleNotification: SimpleNotification): Notification {
            val notification = Notification()
            notification.id = simpleNotification.notification_id
            notification.type = simpleNotification.type
            notification.linked_Object_Id = simpleNotification.linked_object_id

            return notification
        }
    }

    fun getNotificationStatus() : NotificationStatus {
        var status = NotificationStatus.UNREAD
        for (item in NotificationStatus.list) {
            if (item.value == is_Read) {
                status = item
                break
            }
        }
        return status
    }
}
