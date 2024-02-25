package com.moban.handler

import com.moban.model.data.BitmapUpload

/**
 * Created by LenVo on 3/11/18.
 */
interface PostImageItemHandler {
    fun onRemove(bitmap: BitmapUpload)
}
