package com.moban.handler

import com.moban.model.data.user.User

/**
 * Created by LenVo on 6/18/18.
 */
interface UserItemHandler {
    fun onClicked(user: User)
}
