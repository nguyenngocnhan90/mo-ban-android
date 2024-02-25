package com.moban.model.data.secondary

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by lenvo on 2020-02-23.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class SecondaryProject : BaseModel() {
    var id: Int = 0

    @SerializedName("product_Image")
    var product_Image: String = ""

    @SerializedName("product_Name")
    var product_Name: String = ""
}
