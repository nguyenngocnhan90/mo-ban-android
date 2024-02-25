package com.moban.model.response.review

import com.moban.model.BaseModel
import com.moban.model.data.popup.Review

class ReviewResponse: BaseModel() {
    var success: Boolean = false
    var data: Review? = null
}
