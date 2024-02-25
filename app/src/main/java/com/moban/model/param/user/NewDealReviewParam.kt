package com.moban.model.param.user

import com.google.gson.annotations.SerializedName

class NewDealReviewParam {
    @SerializedName("deal_id")
    var deal_id: Int = 0

    @SerializedName("comment")
    var comment: String = ""

    @SerializedName("rating_detail_qldt")
    var rating_detail_qldt: Double = 0.0

    @SerializedName("rating_detail_admin")
    var rating_detail_admin: Double = 0.0
}
