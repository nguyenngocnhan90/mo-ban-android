package com.moban.model.data.secondary.constant

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Attribute: BaseModel() {
    var id: Int = 0

    @SerializedName("attribute_Name")
    var attribute_Name: String = ""
}
