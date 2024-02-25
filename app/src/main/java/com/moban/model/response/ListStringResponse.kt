package com.moban.model.response

import com.moban.model.BaseModel

/**
 * Created by LenVo on 7/26/18.
 */
class ListStringResponse: BaseModel() {
    var success: Boolean = false
    var data: List<String> = ArrayList()
    var strings: List<String> = ArrayList()
}
