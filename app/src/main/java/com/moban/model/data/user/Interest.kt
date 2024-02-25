package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Interest: BaseModel() {
    var id: Int = 0

    @SerializedName("interest_Name")
    var interest_Name: String = ""
}
