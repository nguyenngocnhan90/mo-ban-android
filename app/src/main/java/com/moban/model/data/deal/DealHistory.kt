package com.moban.model.data.deal

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/30/18.
 */
class DealHistory : BaseModel() {
    var id: Int = 0

    @SerializedName("deal_Id")
    var deal_Id: Int = 0

    @SerializedName("deal_Status")
    var deal_Status: Int = 1

    @SerializedName("deal_Date")
    var deal_Date: Double = 0.0

    @SerializedName("deal_Status_Name")
    var deal_Status_Name: String = ""

    @SerializedName("is_Finished")
    var is_Finished: Boolean = false
}
