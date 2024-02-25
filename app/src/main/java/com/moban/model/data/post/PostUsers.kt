package com.moban.model.data.post

import com.moban.model.BaseModel
import com.moban.model.data.user.User

/**
 * Created by LenVo on 6/18/18.
 */
class PostUsers: BaseModel() {
    var users: List<User> = ArrayList()
}
