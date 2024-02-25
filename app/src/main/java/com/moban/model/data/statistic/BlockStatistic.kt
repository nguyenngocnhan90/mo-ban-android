package com.moban.model.data.statistic

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/17/18.
 */
class BlockStatistic: BaseModel() {
    @SerializedName("count_Hopdong")
    var count_Hopdong: Int = 0

    @SerializedName("count_Coc")
    var count_Coc: Int = 0

    @SerializedName("count_Datcho")
    var count_Datcho: Int = 0

    @SerializedName("count_Trong")
    var count_Trong: Int = 0

    @SerializedName("count_Total")
    var count_Total: Int = 0
}
