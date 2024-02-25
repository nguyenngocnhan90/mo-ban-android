package com.moban.model.response.document

import com.moban.model.BaseModel
import com.moban.model.data.document.DocumentGroup

/**
 * Created by LenVo on 7/19/18.
 */
class DocumentGroupResponse: BaseModel() {
    var success: Boolean = false
    var data: List<DocumentGroup> = ArrayList()
}
