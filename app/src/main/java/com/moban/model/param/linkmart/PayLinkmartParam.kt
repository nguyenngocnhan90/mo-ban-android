package com.moban.model.param.linkmart

import com.google.gson.annotations.SerializedName

/**
 * Created by H. Len Vo on 9/23/18.
 */
class PayLinkmartParam {
    @SerializedName("transaction_id")
    var transaction_id: String = ""

    @SerializedName("product_id")
    var product_id: Int = 0

    @SerializedName("product_name")
    var product_name: String = ""

    @SerializedName("lhc")
    var lhc: Int = 0

    @SerializedName("product_categories")
    var product_categories: List<LinkmartCategoryParam> = ArrayList()
}
