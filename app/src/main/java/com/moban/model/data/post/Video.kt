package com.moban.model.data.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Video : BaseModel() {
    @SerializedName("youtube_Id")
    var youtube_Id: String? = null

    @SerializedName("thumb_Image")
    var thumb_Image: String = ""
}
