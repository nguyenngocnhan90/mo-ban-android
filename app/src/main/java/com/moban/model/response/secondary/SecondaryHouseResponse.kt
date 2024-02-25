package com.moban.model.response.secondary

import com.moban.model.BaseModel
import com.moban.model.data.secondary.SecondaryHouse

class SecondaryHouseResponse : BaseModel() {
    var success: Boolean = false
    var data: SecondaryHouse? = null
    var message: String? = null
    var error: String? = null
}
