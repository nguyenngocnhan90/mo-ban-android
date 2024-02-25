package com.moban.model.data.linkmart

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by H. Len Vo on 9/5/18.
 */
class LinkmartImage: BaseModel() {
    @SerializedName("src")
    var url: String = ""
}
