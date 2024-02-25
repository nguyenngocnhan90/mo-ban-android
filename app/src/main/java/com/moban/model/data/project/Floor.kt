package com.moban.model.data.project

import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/17/18.
 */
class Floor: BaseModel() {
    var group: String = ""
    var items: List<Apartment> = ArrayList()
}
