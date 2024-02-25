package com.moban.model.response

import com.moban.model.BaseModel

class BaseListResponse<T: BaseModel> : BaseModel() {
    var success: Boolean = false
    var data: BaseDataList<T>? = null
    var message: String = ""
}
