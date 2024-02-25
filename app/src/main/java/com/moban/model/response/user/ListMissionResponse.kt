package com.moban.model.response.user

import com.moban.model.BaseModel

class ListMissionResponse : BaseModel() {
    var success: Boolean = false
    var data: ListMissions? = null
}
