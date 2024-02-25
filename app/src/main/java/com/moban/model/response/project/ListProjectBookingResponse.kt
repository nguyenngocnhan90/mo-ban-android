package com.moban.model.response.project

import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/19/18.
 */
class ListProjectBookingResponse : BaseModel() {
    var success: Boolean = false
    var error: String = ""
    var data: ListProjectBooking? = null
}
