package com.moban.model.data.gift

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 4/11/18.
 */
class Gift : BaseModel() {
    var id: Int = 0

    @SerializedName("gift_Name")
    var gift_Name: String = ""

    @SerializedName("gift_Image")
    var gift_Image: String = ""

    @SerializedName("gift_Point")
    var gift_Point: Int = 0

    @SerializedName("start_Date")
    var start_Date: String = ""

    @SerializedName("end_Date")
    var end_Date: String = ""

    @SerializedName("gift_Type_Id")
    var gift_Type_Id: Int = 0

    @SerializedName("gift_Type_Name")
    var gift_Type_Name: String = ""

    var quantity: Int = 0

    @SerializedName("gift_level_Id")
    var gift_level_Id: Int = 0

    @SerializedName("linkmart_Id")
    var linkmart_Id: Int = 0

    @SerializedName("using_status")
    var using_status: Int = 0

    @SerializedName("gift_Body")
    var gift_Body: String = ""
}
