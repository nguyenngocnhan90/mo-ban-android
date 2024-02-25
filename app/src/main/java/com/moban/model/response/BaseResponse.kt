package com.moban.model.response

import com.moban.model.BaseModel


class BaseResponse<T: BaseModel>: BaseModel() {
    var success: Boolean = false
    var data: T? = null
    var message: String = ""
    var error: String = ""
}
