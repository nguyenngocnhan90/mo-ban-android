package com.moban.model.data.post

import com.moban.model.BaseModel
import com.moban.model.data.deal.Deal

/**
 * Created by LenVo on 6/18/18.
 */
class PostDeals: BaseModel() {
    var deals: List<Deal> = ArrayList()
}
