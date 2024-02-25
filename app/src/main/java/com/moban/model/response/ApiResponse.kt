package com.moban.model.response

import com.moban.model.BaseModel

class ApiResponse: BaseModel() {
    var success: Boolean = false
    var error: String = ""
    var message: String = ""
    var data: String = ""
}
