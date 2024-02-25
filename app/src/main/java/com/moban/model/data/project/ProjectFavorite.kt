package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 5/15/18.
 */
class ProjectFavorite: BaseModel() {
    var id: Int = 0

    @SerializedName("product_Id")
    var product_Id: Int = 0

    @SerializedName("product_Name")
    var product_Name: String = ""

    @SerializedName("product_Price")
    var product_Price: Int = 0

    @SerializedName("user_Id")
    var user_Id: Int = 0
}
