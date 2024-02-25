package com.moban.model.data.user

import com.moban.model.BaseModel

class InterestGroup: BaseModel() {
    var group: String = ""
    var required: Boolean = false
    var items: List<Interest> = ArrayList()
}
