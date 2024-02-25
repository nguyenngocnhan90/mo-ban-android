package com.moban.model.param.linkmart

import com.google.gson.annotations.SerializedName


/**
 * Created by H. Len Vo on 9/24/18.
 */
class BillingParam {
    @SerializedName("first_name")
    var first_name: String = ""

    @SerializedName("last_name")
    var last_name: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("address_1")
    var address_1: String = ""

    @SerializedName("email")
    var email: String = ""
}
