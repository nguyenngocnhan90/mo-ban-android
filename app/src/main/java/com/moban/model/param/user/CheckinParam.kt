package com.moban.model.param.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/30/18.
 */
class CheckinParam: BaseModel() {
    var key: String = ""
    var ip: String = ""

    var location: String = ""

    @SerializedName("event_type")
    var event_type: String = "QRCode"

    @SerializedName("event_id")
    var event_id: String = ""

    @SerializedName("user_id")
    var user_id: Int = 0
}
