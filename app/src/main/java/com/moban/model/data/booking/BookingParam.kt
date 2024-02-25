package com.moban.model.data.booking

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class BookingParam: BaseModel() {
    @SerializedName("product_id")
    var product_id: Int = 0

    @SerializedName("flat_id")
    var flat_id: Int = 0

    @SerializedName("flat_code")
    var flat_code: String = ""

    @SerializedName("customer_name")
    var customer_name: String = ""

    @SerializedName("customer_phone")
    var customer_phone: String = ""

    @SerializedName("customer_cmnd")
    var customer_cmnd: String = ""

    @SerializedName("customer_birthday")
    var customer_birthday: String? = null
}
