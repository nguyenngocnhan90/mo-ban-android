package com.moban.model.param.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class ProjectRatingParam: BaseModel() {

    @SerializedName("juridical")
    var juridical: Double = 0.0

    @SerializedName("host_Trust")
    var host_Trust: Double = 0.0

    @SerializedName("location")
    var location: Double = 0.0

    @SerializedName("price")
    var price: Double = 0.0

    @SerializedName("design_Utility")
    var design_Utility: Double = 0.0
}