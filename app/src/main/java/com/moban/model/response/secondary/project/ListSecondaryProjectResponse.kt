package com.moban.model.response.secondary.project

import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/7/18.
 */
class ListSecondaryProjectResponse : BaseModel() {
    var success: Boolean = false
    var data: ListSecondaryProject? = null
}
