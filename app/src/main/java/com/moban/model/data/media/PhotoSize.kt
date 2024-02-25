package com.moban.model.data.media

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by neal on 3/6/18.
 */
class PhotoSize : BaseModel() {
    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0
}
