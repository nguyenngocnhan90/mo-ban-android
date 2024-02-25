package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.user.InterestGroup

/**
 * Created by LenVo on 2/20/19.
 */
class ListInterests : BaseModel() {
    var list: List<InterestGroup> = ArrayList()
}
