package com.moban.model.response.project

import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/7/18.
 */
class ListProjectResponse : BaseModel() {
    var success: Boolean = false
    var data: ListProject? = null
}
