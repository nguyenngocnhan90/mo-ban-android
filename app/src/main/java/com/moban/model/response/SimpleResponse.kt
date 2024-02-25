package com.moban.model.response

import com.moban.model.BaseModel

class SimpleResponse: BaseModel() {
    var success: Boolean = false
    var data: String? = null
    var error: String? = null
}
