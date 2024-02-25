package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class LinkPointTransaction : BaseModel() {
    var id: Int = 0

//    @SerializedName("created_Date")
//    var created_Date: String = ""

    @SerializedName("description")
    var description: String = ""

    @SerializedName("target_Id")
    var target_Id: Int = 0

    @SerializedName("target_Type")
    var target_Type: String = ""

    @SerializedName("linkPoint")
    var linkPoint: Int = 0

}
