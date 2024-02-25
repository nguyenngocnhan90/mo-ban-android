package com.moban.model.data.popup

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.media.Photo

class Popup: BaseModel() {
    var id: Int = 0

    @SerializedName("user_id")
    var user_id: Int = 0

    @SerializedName("object_id")
    var object_id: Int = 0

    @SerializedName("type")
    var type: String = ""

    @SerializedName("content")
    var content: String = ""

    @SerializedName("photo")
    var photo: Photo? = null

    @SerializedName("is_read")
    var is_read: Boolean = false

    @SerializedName("review")
    var review: Review? = null
}
