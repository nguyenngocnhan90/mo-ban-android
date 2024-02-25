package com.moban.model.response.user

import com.moban.model.BaseModel
import com.moban.model.data.user.LevelGift

class LevelGiftResponse: BaseModel() {
    var success: Boolean = false
    var data: LevelGift? = null
}
