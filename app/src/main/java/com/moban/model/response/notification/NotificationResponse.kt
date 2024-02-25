package com.moban.model.response.notification

import com.moban.model.BaseModel
import com.moban.model.data.notification.Notification

/**
 * Created by LenVo on 5/11/18.
 */
class NotificationResponse : BaseModel() {
    var success: Boolean = false
    var data: Notification? = null
    var error: String? = null
}
