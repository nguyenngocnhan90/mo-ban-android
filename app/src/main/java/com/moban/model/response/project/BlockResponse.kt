package com.moban.model.response.project

import com.moban.model.BaseModel
import com.moban.model.data.project.BlockData

/**
 * Created by LenVo on 7/17/18.
 */
class BlockResponse: BaseModel() {
    var success: Boolean = false
    var data: BlockData? = null
}
