package com.moban.model.response

import com.moban.model.data.user.User

/**
 * Created by neal on 3/3/18.
 */
class LoginResponse {
    var success: Boolean = false
    var data: User? = null
    var error: String? = null
}
