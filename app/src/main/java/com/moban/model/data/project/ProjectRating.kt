package com.moban.model.data.project

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class ProjectRating: BaseModel() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("average")
    var average: Double = 0.0

    @SerializedName("count")
    var count: Int = 0

    @SerializedName("detail")
    var detail: ProjectRatingDetail? = null

    @SerializedName("post_Id")
    var post_Id: Int = 0
}