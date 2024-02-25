package com.moban.model.data.secondary.constant

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class HouseType: BaseModel() {
    var id: Int = 0

    @SerializedName("house_Type_Name")
    var house_Type_Name: String = ""
}
