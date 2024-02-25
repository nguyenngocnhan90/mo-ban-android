package com.moban.model.data.linkmart

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by H. Len Vo on 9/6/18.
 */
class LinkmartOrder: BaseModel() {
    var id: String = ""

    @SerializedName("currency")
    var currency: String = ""

    @SerializedName("total")
    var total: String = ""

    @SerializedName("line_items")
    var line_items: List<Linkmart>? = null
}
