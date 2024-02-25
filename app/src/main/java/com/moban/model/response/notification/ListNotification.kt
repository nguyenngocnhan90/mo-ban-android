package com.moban.model.response.notification

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.notification.Notification

/**
 * Created by LenVo on 5/11/18.
 */
class ListNotification : BaseModel() {
    var list: List<Notification> = ArrayList()

    @SerializedName("unread_Count")
    var unread_Count: Int = 0

    var paging: Paging? = null
}
