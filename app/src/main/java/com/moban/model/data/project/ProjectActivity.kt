package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by lenvo on 7/1/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class ProjectActivity : BaseModel() {
    var id: Int = 0

    @SerializedName("user_Profile_Name")
    var user_Profile_Name: String = ""

    @SerializedName("user_Profile_Avatar")
    var user_Profile_Avatar: String = ""

    @SerializedName("description")
    var activity_Description: String = ""

    @SerializedName("created_Date")
    var created_Date: Double = 0.0
}
