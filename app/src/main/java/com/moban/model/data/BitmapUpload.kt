package com.moban.model.data

import android.graphics.Bitmap
import com.moban.model.data.media.Photo

/**
 * Created by LenVo on 5/30/18.
 */
class BitmapUpload {
    constructor()

    constructor(time: Long, bitmap: Bitmap) {
        this.time = time
        this.bitmap = bitmap
    }

    var time: Long = 0
    var url: String = ""
    var bitmap: Bitmap? = null
    var photo: Photo? = null
    var uploaded: Boolean = false

    fun isUploaded(): Boolean {
        return uploaded || url.isNotEmpty()
    }
}
