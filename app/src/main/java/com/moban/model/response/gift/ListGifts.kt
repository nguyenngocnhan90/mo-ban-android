package com.moban.model.response.gift

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.gift.Gift

/**
 * Created by LenVo on 4/11/18.
 */
class ListGifts : BaseModel() {
    var list: List<Gift> = ArrayList()
    var paging: Paging? = null
}
