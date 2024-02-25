package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/5/18.
 */
class Block: BaseModel() {
    var id: Int = 0

    @SerializedName("block_Name")
    var block_Name: String = ""

    @SerializedName("floorList")
    var floorList: List<String> = ArrayList()
}
