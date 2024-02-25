package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.user.LevelGift

/**
 * Created by LenVo on 3/30/19.
 */
class ListLevelGifts : BaseModel() {
    var list: List<LevelGift> = ArrayList()
    var paging: Paging? = null
}
