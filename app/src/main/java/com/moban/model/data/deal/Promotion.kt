package com.moban.model.data.deal

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Promotion: BaseModel() {
    var id: Int = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("image")
    var image: String = ""
}