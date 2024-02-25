package com.moban.model.param.user

import com.google.gson.annotations.SerializedName

class TransferCoinParam {
    @SerializedName("to_User_Id")
    var to_User_Id: Int = 0

    @SerializedName("linkCoin")
    var linkCoin: Int = 0

    @SerializedName("message")
    var message: String = ""

    @SerializedName("description")
    var description: String = ""
}
