package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/29/18.
 */
class LhcTransaction : BaseModel() {
    var id: Int = 0

    @SerializedName("created_Date")
    var created_Date: Double = 0.0

    var description: String = ""

    @SerializedName("user_Id")
    var user_Id: Int = 0

    @SerializedName("target_Id")
    var target_Id: Int = 0

    @SerializedName("target_Type")
    var target_Type: String = ""

    @SerializedName("mission_Name")
    var mission_Name: String = ""

    @SerializedName("linkCoin")
    var linkCoin: Int = 0

    @SerializedName("gift_Name")
    var gift_Name: String = ""
}
