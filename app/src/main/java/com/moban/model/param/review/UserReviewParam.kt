package com.moban.model.param.review

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.popup.Review

class UserReviewParam(): BaseModel() {

    constructor(review: Review): this() {
        id = review.id
        object_id = review.object_id
        parent_id = review.parent_id
        to_user_id = review.to_user_id
        review_type = review.review_type
        view_type = review.view_type
    }

    var id: Int = 0

    @SerializedName("object_id")
    var object_id: Int = 0

    @SerializedName("parent_id")
    var parent_id: Int = 0

    @SerializedName("to_user_id")
    var to_user_id: Int = 0

    @SerializedName("comment")
    var comment: String = ""

    @SerializedName("rating")
    var rating: Double = 0.0

    @SerializedName("review_type")
    var review_type: String = ""

    @SerializedName("view_type")
    var view_type: Int = 0
}
