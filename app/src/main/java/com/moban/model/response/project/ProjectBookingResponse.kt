package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.booking.ProjectBooking

/**
 * Created by LenVo on 3/19/18.
 */
class ProjectBookingResponse : BaseModel() {
    var success: Boolean = false
    var error: String = ""
    var message: String = ""
    var data: ProjectBooking? = null
}
