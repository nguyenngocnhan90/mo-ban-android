package com.moban.model.data.address

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/26/18.
 */
class City: BaseModel() {
    var id: Int = 0

    @SerializedName("city_Name")
    var city_Name: String = ""
}
