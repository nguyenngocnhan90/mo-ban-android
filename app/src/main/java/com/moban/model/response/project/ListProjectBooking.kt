package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.booking.ProjectBooking

/**
 * Created by LenVo on 3/7/18.
 */
class ListProjectBooking : BaseModel() {
    var list: List<ProjectBooking> = ArrayList()
    var paging: Paging? = null
}
