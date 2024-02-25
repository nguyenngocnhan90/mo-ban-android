package com.moban.model.param.user

import com.google.gson.annotations.SerializedName

class RecoverPasswordParam {
    @SerializedName("username")
    var username: String = ""

    @SerializedName("otp")
    var otp: String = ""

    @SerializedName("new_password")
    var new_password: String = ""
}
