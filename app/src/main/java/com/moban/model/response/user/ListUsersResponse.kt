package com.moban.model.response.user

import com.moban.model.BaseModel

/**
 * Created by LenVo on 5/11/18.
 */
class ListUsersResponse : BaseModel() {
    var success: Boolean = false
    var data: ListUsers? = null
}
