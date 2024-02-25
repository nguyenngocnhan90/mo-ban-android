package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.Paging

class ListLevelGiftResponse: BaseModel() {
    var success: Boolean = false
    var data: ListLevelGifts? = null
    var min_id: Int = 0
    var paging: Paging? = null
    var error: String? = null
}
