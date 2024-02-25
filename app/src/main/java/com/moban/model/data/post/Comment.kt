package com.moban.model.data.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.media.Photo

/**
 * Created by neal on 3/14/18.
 */
class Comment : BaseModel() {
    var id: Int = 0

    @SerializedName("user_Id")
    var user_Id: Int = 0

    @SerializedName("user_Profile_Name")
    var user_Profile_Name: String = ""
    var avatar: String = ""

    @SerializedName("comment_Content")
    var comment_Content: String = ""

    @SerializedName("photo")
    var photo: Photo? = null

    @SerializedName("created_Date")
    var created_Date: Double = 0.0

    @SerializedName("sub_Comments")
    var sub_Comments: List<Comment>? = null

    val comment_Count: Int
        get() = sub_Comments?.count() ?: 0
}
