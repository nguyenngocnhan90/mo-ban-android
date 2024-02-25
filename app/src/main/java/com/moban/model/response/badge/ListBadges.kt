package com.moban.model.response.badge

import com.moban.model.BaseModel
import com.moban.model.data.user.Badge

class ListBadges: BaseModel() {
    var list: List<Badge> = ArrayList()
}
