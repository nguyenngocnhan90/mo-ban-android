package com.moban.model.param.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class GetOTPParam: BaseModel() {
    @SerializedName("username")
    var username: String = ""
}
