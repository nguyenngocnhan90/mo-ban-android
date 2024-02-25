package com.moban.handler

import com.moban.model.data.Photo

interface PhotoItemHandler {
    fun onClicked(photo: Photo, pos: Int)
}
