package com.moban.model.data.document

import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/19/18.
 */
class DocumentGroup: BaseModel() {
    var group: String = ""
    var items: List<Document> = ArrayList()
}
