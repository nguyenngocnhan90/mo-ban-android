package com.moban.model.data.address

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/26/18.
 */
class District: BaseModel() {
    var id: Int = 0

    @SerializedName("district_Name")
    var district_Name: String = ""
}
