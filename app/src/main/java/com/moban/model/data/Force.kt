package com.moban.model.data

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 6/2/18.
 */
class Force : BaseModel() {
    @SerializedName("latest_version")
    var latest_version: Int = 0

    var force: Boolean = false
}
