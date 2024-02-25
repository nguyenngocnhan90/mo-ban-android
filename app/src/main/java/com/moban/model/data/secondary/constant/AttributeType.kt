package com.moban.model.data.secondary.constant

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class AttributeType : BaseModel() {
    var id: Int = 0

    @SerializedName("attribute_Type_Name")
    var attribute_Type_Name: String = ""

    @SerializedName("category")
    var category: Int = 0

    @SerializedName("attributes")
    var attributes: List<Attribute> = ArrayList()
}
