package com.moban.model.data.salary

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class PersonalRevenue : BaseModel() {
    @SerializedName("product_Revenue")
    var product_Revenue: Int = 0

    @SerializedName("product_Name")
    var product_Name: String = ""
}
