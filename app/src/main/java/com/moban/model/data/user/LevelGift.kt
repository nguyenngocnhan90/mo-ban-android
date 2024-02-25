package com.moban.model.data.user

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class LevelGift : BaseModel() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("gift_Name")
    var gift_Name: String = ""

    @SerializedName("gift_Image")
    var gift_Image: String = ""

    @SerializedName("gift_Point")
    var gift_Point: Int = 0

    @SerializedName("start_Date")
    var start_Date: Double = 0.0

    @SerializedName("end_Date")
    var end_Date: Double = 0.0

    @SerializedName("gift_Type_Id")
    var gift_Type_Id: Int = 0

    @SerializedName("gift_Type_Name")
    var gift_Type_Name: String = ""

    @SerializedName("quantity")
    var quantity: Int = 0

    @SerializedName("using_status")
    var using_status: Int = 0

    @SerializedName("gift_level_Id")
    var gift_level_Id: Int = 0

    @SerializedName("gift_Id")
    var gift_Id: Int = 0

    @SerializedName("linkmart_Id")
    var linkmart_Id: Int = 0

    @SerializedName("gift_Body")
    var gift_Body: String = ""

    @SerializedName("gift_Value")
    var gift_Value: Int = 0

    @SerializedName("gift_Value_Deal")
    var gift_Value_Deal: Int? = null

    var isBookingPromotion: Boolean = false
        get() {
            return gift_Type_Id == 6
        }
}
