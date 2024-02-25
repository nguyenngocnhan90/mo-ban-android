package com.moban.handler

import com.moban.model.data.project.Block

/**
 * Created by LenVo on 3/11/18.
 */
interface BlockItemHandler {
    fun onClicked(block: Block?, position: Int)
}
