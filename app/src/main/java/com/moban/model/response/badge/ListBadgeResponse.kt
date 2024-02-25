package com.moban.model.response.badge

import com.moban.model.BaseModel

class ListBadgeResponse: BaseModel() {
    var success: Boolean = false
    var data: ListBadges? = null
}
