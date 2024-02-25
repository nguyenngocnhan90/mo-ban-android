package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Badge: BaseModel() {
    var id: Int = 0

    @SerializedName("badge_Id")
    var badge_Id: Int = 0

    @SerializedName("badge_Name")
    var badge_Name: String = ""

    @SerializedName("badge_Image")
    var badge_Image: String = ""

    @SerializedName("created_Date")
    var created_Date: Double = 0.0

    @SerializedName("mission_Id")
    var mission_Id: Int = 0

    @SerializedName("mission_Name")
    var mission_Name: String = ""
}
