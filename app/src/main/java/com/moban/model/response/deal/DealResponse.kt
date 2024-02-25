package com.moban.model.response.deal

import com.moban.model.BaseModel
import com.moban.model.data.deal.Deal

/**
 * Created by LenVo on 4/06/18.
 */
class DealResponse : BaseModel() {
    var success: Boolean = false
    var data: Deal? = null
    var error: String = ""
}
