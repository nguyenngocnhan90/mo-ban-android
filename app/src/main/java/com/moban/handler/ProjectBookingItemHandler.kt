package com.moban.handler

import com.moban.model.data.booking.ProjectBooking

/**
 * Created by LenVo on 3/21/18.
 */
interface ProjectBookingItemHandler {
    fun onClicked(projectBooking: ProjectBooking)
}
