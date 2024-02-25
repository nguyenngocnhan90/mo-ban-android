package com.moban.model.data.news

import com.moban.model.BaseModel

/**
 * Created by LenVo on 6/21/18.
 */
class News : BaseModel() {
    var id: Int = 0
    var url: String = ""
    var title: String = ""
    var domain: String = ""
}
