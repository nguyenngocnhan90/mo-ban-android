package com.moban.handler

import com.moban.model.data.project.Floor

/**
 * Created by LenVo on 3/11/18.
 */
interface FloorItemHandler {
    fun onClicked(floor: Floor, position: Int)
}
