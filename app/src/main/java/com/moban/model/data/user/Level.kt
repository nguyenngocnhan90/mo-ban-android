package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Level : BaseModel() {
    var id: Int = 0

    @SerializedName("level_Name")
    var level_Name: String = ""

    @SerializedName("linkPoint")
    var linkPoint: Int = 0

    @SerializedName("level_Point")
    var level_Point: Int = 0

    @SerializedName("revenue")
    var revenue: Int = 0

    @SerializedName("management_User_Count")
    var management_User_Count: Int = 0

    @SerializedName("user_Rating")
    var user_Rating: Int = 0

    @SerializedName("bonus_Fee")
    var bonus_Fee: Int = 0

    @SerializedName("deposit_Rate")
    var deposit_Rate: Int = 0

    @SerializedName("salary")
    var salary: Int = 0

    @SerializedName("bonus_Salary")
    var bonus_Salary: Int = 0

    @SerializedName("next_Level")
    var next_Level: Level? = null
}
