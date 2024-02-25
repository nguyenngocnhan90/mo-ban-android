package com.moban.model.response.user

import com.moban.model.BaseModel

/**
 * Created by LenVo on 2/20/19.
 */
class ListInterestsResponse : BaseModel() {
    var success: Boolean = false
    var data: ListInterests? = null
}
