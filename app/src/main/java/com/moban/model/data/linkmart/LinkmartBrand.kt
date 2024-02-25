package com.moban.model.data.linkmart

import com.moban.model.BaseModel

/**
 * Created by H. Len Vo on 9/25/18.
 */
class LinkmartBrand: BaseModel() {
    var id: Int = 0
    var name: String = ""
    var description: String = ""
    var image: LinkmartImage? = null
}
