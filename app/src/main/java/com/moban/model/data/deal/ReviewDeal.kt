package com.moban.model.data.deal

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class ReviewDeal: BaseModel() {
    var id: Int = 0

    @SerializedName("comment")
    var comment: String = ""

    @SerializedName("rating_Detail_Admin")
    var rating_Detail_Admin: Double = 0.0

    @SerializedName("rating_Detail_QLDT")
    var rating_Detail_QLDT: Double = 0.0

    @SerializedName("rating_Overall")
    var rating_Overall: Double = 0.0

    @SerializedName("deal_Id")
    var deal_Id: Int = 0
}
