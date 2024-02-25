package com.moban.model.response.badge

import com.moban.model.BaseModel
import com.moban.model.data.user.Badge

/**
 * Created by LenVo on 4/23/18.
 */
class BadgeResponse : BaseModel() {
    var success: Boolean = false
    var data: Badge? = null
}
