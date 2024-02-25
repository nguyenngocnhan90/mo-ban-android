package com.moban.model.data.salary

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class GroupRevenue : BaseModel() {
    @SerializedName("group_Revenue")
    var group_Revenue: Int = 0

    @SerializedName("leader_Commission_Rate")
    var leader_Commission_Rate: Int = 0

    @SerializedName("leader_Commission")
    var leader_Commission: Int = 0

    @SerializedName("revenue_2_Last_Month")
    var revenue_2_Last_Month: Int = 0

    var coefficients: Int = 0

    @SerializedName("hard_Wage")
    var hard_Wage: Int = 0
}
