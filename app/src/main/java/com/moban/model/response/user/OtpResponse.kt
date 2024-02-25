package com.moban.model.response.user

import com.moban.model.BaseModel

class OtpResponse: BaseModel() {

    var success: Boolean = false
    var data: String? = null
    var error: String? = null
    var otp: String = ""
}