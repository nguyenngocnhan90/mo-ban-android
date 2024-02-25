package com.moban.handler

import com.moban.model.data.user.Mission

/**
 * Created by LenVo on 4/24/18.
 */
interface MissionItemHandler {
    fun onClicked(mission: Mission)
}
