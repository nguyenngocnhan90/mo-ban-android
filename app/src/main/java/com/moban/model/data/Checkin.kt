package com.moban.model.data

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.linkmart.LinkmartCategory

class Checkin: BaseModel() {
    @SerializedName("linkmart_category")
    var linkmart_category: LinkmartCategory? = null
}
