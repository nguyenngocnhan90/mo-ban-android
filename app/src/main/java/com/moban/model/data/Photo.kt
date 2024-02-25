package com.moban.model.data

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 3/22/18.
 */
class Photo : BaseModel {
    constructor()

    constructor(url: String) {
        this.photo_Link = url
    }
    var id: Int = 0

    var width: Long = 0
    var height: Long = 0

    @SerializedName("photo_Link")
    var photo_Link: String = ""

    @SerializedName("photo_Thumb")
    var photo_Thumb: String = ""
}
