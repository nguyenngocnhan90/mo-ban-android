package com.moban.model.data.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Topic: BaseModel() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("topic")
    var topic: String = ""
}