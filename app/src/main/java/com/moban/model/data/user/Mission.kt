package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Mission : BaseModel(){
    var id: Int = 0

    @SerializedName("created_Date")
    var created_Date: Double = 0.0

    @SerializedName("linkCoin")
    var linkCoin: Int = 0

    @SerializedName("mission_Content")
    var mission_Content: String = ""

    @SerializedName("mission_Name")
    var mission_Name: String = ""

    var status: Int = 0

    @SerializedName("start_Date")
    var start_Date: Double = 0.0

    @SerializedName("end_Date")
    var end_Date: Double = 0.0

    @SerializedName("mission_Type_Id")
    var mission_Type_Id: Int = 0

    @SerializedName("mission_Type_Name")
    var mission_Type_Name: String = ""

    @SerializedName("badge_Id")
    var badge_Id: Int = 0

    @SerializedName("badge_Name")
    var badge_Name: String = ""

    @SerializedName("badge_Image")
    var badge_Image: String = ""
}
