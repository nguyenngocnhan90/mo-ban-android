package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.user.User

/**
 * Created by LenVo on 5/11/18.
 */
class ListUsers : BaseModel() {
    var list: List<User> = ArrayList()
    var paging: Paging? = null
}
