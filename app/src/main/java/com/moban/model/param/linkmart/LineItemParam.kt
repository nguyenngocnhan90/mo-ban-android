package com.moban.model.param.linkmart

import com.google.gson.annotations.SerializedName

/**
 * Created by H. Len Vo on 9/24/18.
 */
class LineItemParam {
    @SerializedName("product_id")
    var product_id: Int = 0

    @SerializedName("quantity")
    var quantity: Int = 1
}
