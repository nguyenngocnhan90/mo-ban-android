package com.moban.model.data.secondary.constant

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class ProjectBase: BaseModel() {
    var id: Int = 0

    @SerializedName("product_Name")
    var product_Name: String = ""
}
