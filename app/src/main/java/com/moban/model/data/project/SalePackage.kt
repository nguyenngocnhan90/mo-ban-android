package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by lenvo on 7/1/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class SalePackage : BaseModel() {
    var code: String = ""

    @SerializedName("subscriber_Name")
    var subscriber_Name: String = ""

    @SerializedName("subscriber_Type")
    var subscriber_Type: String = ""

    @SerializedName("subscriber_Content")
    var subscriber_Content: String = ""

    @SerializedName("subscriber_Content_Label")
    var subscriber_Content_Label: String = ""

    @SerializedName("subscriber_Description")
    var subscriber_Description: String = ""
}
