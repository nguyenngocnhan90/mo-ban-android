package com.moban.model.response.notification

import com.moban.model.BaseModel

/**
 * Created by LenVo on 5/11/18.
 */
class ListNotificationResponse : BaseModel() {
    var success: Boolean = false
    var data: ListNotification? = null
}
