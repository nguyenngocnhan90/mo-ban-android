package com.moban.model.data.media

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by neal on 3/6/18.
 */

class Photo : BaseModel() {
    var id: Int = 0

    @SerializedName("photo_Link")
    var photo_Link: String = ""

    @SerializedName("photo_Thumb")
    var photo_Thumb: String = ""

    @SerializedName("photo_Size")
    var photo_Size: PhotoSize? = null

    val width: Int
        get() = photo_Size?.width ?: 0

    val height: Int
        get() = photo_Size?.height ?: 0
}
