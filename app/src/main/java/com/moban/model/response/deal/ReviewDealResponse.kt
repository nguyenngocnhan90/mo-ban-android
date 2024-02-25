package com.moban.model.response.deal

import com.moban.model.BaseModel
import com.moban.model.data.deal.ReviewDeal

class ReviewDealResponse : BaseModel() {
    var success: Boolean = false
    var data: ReviewDeal? = null
}
