package com.moban.model.param.linkmart

import com.moban.model.data.linkmart.LinkmartCategory

class LinkmartCategoryParam {
    var id: Int = 0
    var name: String = ""

    constructor(category: LinkmartCategory) {
        id = category.id
        name = category.name
    }
}
