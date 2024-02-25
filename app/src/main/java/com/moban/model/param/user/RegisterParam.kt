package com.moban.model.param.user

import com.google.gson.annotations.SerializedName
import com.moban.constant.Constant
import com.moban.helper.LocalStorage
import com.moban.model.BaseModel
import com.moban.util.Device

class RegisterParam: BaseModel() {
    @SerializedName("name")
    var name: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("ref_name")
    var ref_name: String = ""

    @SerializedName("ref_id")
    var ref_id: Int = 0

    @SerializedName("source")
    var source: String = ""

    @SerializedName("otp")
    var otp: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("device_token")
    var device_token: String? = LocalStorage.googleRegistrationId()

    @SerializedName("device_model")
    var device_model : String? = Device.getDeviceName()

    @SerializedName("platform")
    val platform = Constant.platform

    @SerializedName("one_signal_id")
    var one_signal_id: String? = LocalStorage.oneSignalId()
}
