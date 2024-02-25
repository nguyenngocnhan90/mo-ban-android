package com.moban.model.response

import com.moban.model.BaseModel
import com.moban.model.data.Force

/**
 * Created by LenVo on 6/2/18.
 */
class ForceResponse: BaseModel() {
    var success: Boolean = false
    var data: Force? = null
    var error: String? = null
}
