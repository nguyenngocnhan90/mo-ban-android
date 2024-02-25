package com.moban.model.param

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class NewReservationParam: BaseModel() {
    var id: Int = 0

    @SerializedName("deal_status")
    var deal_status: Int = 0

    @SerializedName("number_of_flat")
    var number_of_flat: Int = 1

    @SerializedName("product_id")
    var product_id: Int = 0

    @SerializedName("deal_price")
    var deal_price: Int? = null

    @SerializedName("reason")
    var reason: String = ""

    @SerializedName("customer_name")
    var customer_name: String = ""

    @SerializedName("customer_cmnd")
    var customer_cmnd: String = ""

    @SerializedName("customer_cmnd_place")
    var customer_cmnd_place: String = ""

    @SerializedName("customer_birthday")
    var customer_birthday: String = ""

    @SerializedName("customer_address")
    var customer_address: String = ""

    @SerializedName("customer_address_city")
    var customer_address_city: String = ""

    @SerializedName("customer_address_district")
    var customer_address_district: String = ""

    @SerializedName("customer_email")
    var customer_email: String = ""

    @SerializedName("customer_phone")
    var customer_phone: String = ""

    @SerializedName("customer_cmnd_date")
    var customer_cmnd_date: String = ""

    @SerializedName("customer_address_permanent")
    var customer_address_permanent: String = ""

    @SerializedName("customer_address_permanent_city")
    var customer_address_permanent_city: String = ""

    @SerializedName("customer_address_permanent_district")
    var customer_address_permanent_district: String = ""

    @SerializedName("customer_payment_method")
    var customer_payment_method: String = ""

    @SerializedName("customer_interested")
    var customer_interested: String = ""

    @SerializedName("promotion_id")
    var promotion_id: Int = 0

    @SerializedName("upload_files")
    var upload_files: List<DocumentParam> = ArrayList()

    @SerializedName("deal_interest")
    var deal_interest: List<InterestParam> = ArrayList()
}
