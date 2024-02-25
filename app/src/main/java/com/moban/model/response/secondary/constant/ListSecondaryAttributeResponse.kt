package com.moban.model.response.secondary.constant

import com.moban.model.BaseModel

class ListSecondaryAttributeResponse : BaseModel() {
    var success: Boolean = false
    var data: ListSecondaryAttribute? = null
}
