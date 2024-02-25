package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.user.Mission

/**
 * Created by LenVo on 4/24/18.
 */
class MissionResponse : BaseModel() {
    var success: Boolean = false
    var data: Mission? = null
}
