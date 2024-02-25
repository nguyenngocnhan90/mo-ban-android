package com.moban.model.response

import com.moban.model.BaseModel
import com.moban.model.data.Checkin

class CheckInResponse: BaseModel() {
    var success: Boolean = false
    var data: Checkin? = null
    var error: String? = null
    var message: String? = null
}
