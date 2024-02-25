package com.moban.model.param.user

import com.google.gson.annotations.SerializedName

class UseGiftParam {
    @SerializedName("user_id")
    var user_id: Int = 0

    @SerializedName("gift_level_id")
    var gift_level_id: Int = 0

    @SerializedName("gift_Id")
    var gift_Id: Int = 0
}
