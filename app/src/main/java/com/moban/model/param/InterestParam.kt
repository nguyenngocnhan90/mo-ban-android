package com.moban.model.param

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.user.Interest

class InterestParam: BaseModel {
    @SerializedName("interest_id")
    var interest_id: Int = 0

    var value: String = ""

    constructor(interest: Interest):  super() {
        interest_id = interest.id
        value = interest.interest_Name
    }
}
