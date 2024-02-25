package com.moban.model.data.notification

import com.moban.model.BaseModel

/**
 * Created by thangnguyen on 1/19/18.
 */
class SimpleNotification: BaseModel() {
    var type: String = ""
    var linked_object_id: Int = 0
    var notification_id: Int = 0
    var user_id: Int = 0
}
