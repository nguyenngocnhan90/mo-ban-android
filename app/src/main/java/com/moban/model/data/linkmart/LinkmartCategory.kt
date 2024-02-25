package com.moban.model.data.linkmart

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by H. Len Vo on 9/5/18.
 */
class LinkmartCategory: BaseModel() {
    var id: Int = 0
    var name: String = ""
    var description: String = ""
    var image: LinkmartImage? = null
    var count: Int = 0

    @SerializedName("menu_order")
    var menu_order: Int = 0
}
