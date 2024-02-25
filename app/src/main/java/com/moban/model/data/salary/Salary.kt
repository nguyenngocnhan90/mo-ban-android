package com.moban.model.data.salary

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Salary : BaseModel(){
    var salary: BasicSalary? = null

    @SerializedName("group_revenue")
    var group_revenue: GroupRevenue? = null

    @SerializedName("personal_revenue")
    var personal_revenue: List<PersonalRevenue> = ArrayList()

    @SerializedName("subtotal_revenue")
    var subtotal_revenue: Int = 0

    var tax: Int = 0

    @SerializedName("total_revenue")
    var total_revenue: Int = 0
}
