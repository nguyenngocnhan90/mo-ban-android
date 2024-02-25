package com.moban.model.response.gift

import com.moban.model.BaseModel

/**
 * Created by LenVo on 4/11/18.
 */
class ListGiftsResponse : BaseModel() {
    var success: Boolean = false
    var data: ListGifts? = null
}
