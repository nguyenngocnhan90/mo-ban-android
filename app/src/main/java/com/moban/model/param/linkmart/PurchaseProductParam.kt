package com.moban.model.param.linkmart

import com.google.gson.annotations.SerializedName

/**
 * Created by H. Len Vo on 9/6/18.
 */
class PurchaseProductParam {
    @SerializedName("set_paid")
    var set_paid: Boolean = true

    @SerializedName("customer_id")
    var customer_id: String = ""

    @SerializedName("billing")
    var billing: BillingParam? = null

    @SerializedName("line_items")
    var line_items: MutableList<LineItemParam> = ArrayList()
}
