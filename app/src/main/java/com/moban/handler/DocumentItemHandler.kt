package com.moban.handler

import com.moban.model.data.document.Document

/**
 * Created by LenVo on 3/11/18.
 */
interface DocumentItemHandler {
    fun onClicked(document: Document)
}
