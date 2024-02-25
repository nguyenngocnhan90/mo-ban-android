package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.response.deal.ListDeals

/**
 * Created by LenVo on 3/30/18.
 */
class ListDealsResponse : BaseModel() {
    var success: Boolean = false
    var data: ListDeals? = null
}
