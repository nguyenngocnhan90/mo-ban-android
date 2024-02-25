package com.moban.model.response.secondary.project

import com.moban.model.BaseModel
import com.moban.model.data.secondary.SecondaryProject

class SecondaryProjectResponse : BaseModel() {
    var success: Boolean = false
    var data: SecondaryProject? = null
    var message: String? = null
    var error: String? = null
}
