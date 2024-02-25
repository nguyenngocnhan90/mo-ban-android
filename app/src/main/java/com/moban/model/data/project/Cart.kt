package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/17/18.
 */
class Cart: BaseModel() {
    var id: Int = 0

    @SerializedName("flat_Image")
    var flat_Image: String = ""

    @SerializedName("product_Name")
    var product_Name: String = ""

    @SerializedName("block_Name")
    var block_Name: String = ""

    @SerializedName("flat_Name")
    var flat_Name: String = ""

    @SerializedName("flat_Price")
    var flat_Price: Int = 0

    @SerializedName("flat_Status")
    var flat_Status: Int = 0

    @SerializedName("cart_Status")
    var cart_Status: Int = 0

    @SerializedName("user_Phone")
    var user_Phone: String = ""

    @SerializedName("customer_Name")
    var customer_Name: String = ""

    @SerializedName("customer_Phone")
    var customer_Phone: String = ""

    @SerializedName("customer_Invoice")
    var customer_Invoice: String = ""

    @SerializedName("customer_Invoice_Booking")
    var customer_Invoice_Booking: String = ""
}
