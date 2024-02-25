package com.moban.handler

import com.moban.model.data.BitmapUpload

interface PhotoHorizontalHandler {
    fun onAddPhoto()
    fun onDeletePhoto(bitmap: BitmapUpload)
    fun onSetMainPhoto(bitmap: BitmapUpload)
}
