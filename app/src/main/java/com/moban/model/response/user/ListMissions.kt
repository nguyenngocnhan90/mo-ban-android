package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.user.Mission
import com.moban.model.data.user.MissionCount

class ListMissions: BaseModel() {
    var completed: List<Mission> = ArrayList()
    var inprocess: List<Mission> = ArrayList()
    var expired: List<Mission> = ArrayList()
    var effective: List<Mission> = ArrayList()

    var count: MissionCount? = null
}
