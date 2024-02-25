package com.moban.model.data.salary

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class BasicSalary: BaseModel() {
    var id: Int = 0

    @SerializedName("current_Date")
    var current_Date: String = ""

    @SerializedName("revenue_2_Last_Month")
    var revenue_2_Last_Month: Int = 0

    @SerializedName("revenue_Current_Date")
    var revenue_Current_Date: Int = 0
    var coefficients: Int = 0

    @SerializedName("hard_Wage")
    var hard_Wage: Int = 0

    @SerializedName("policy_Link")
    var policy_Link: String = ""
}
