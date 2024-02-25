package com.moban.model.data.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.user.User

/**
 * Created by LenVo on 6/18/18.
 */
class PostRanks : BaseModel() {
    var quarter: Int = 0

    @SerializedName("current_User")
    var current_User: User? = null

    @SerializedName("other_Users")
    var other_Users: List<User> = ArrayList()
}
