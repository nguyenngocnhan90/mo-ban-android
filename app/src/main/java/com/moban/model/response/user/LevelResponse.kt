package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.user.Level

class LevelResponse: BaseModel() {
    var success: Boolean = false
    var data: Level? = null
}
