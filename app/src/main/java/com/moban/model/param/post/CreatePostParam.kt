package com.moban.model.param.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by LenVo on 4/17/18.
 */
class CreatePostParam: BaseModel() {
    @SerializedName("post_Content")
    var post_Content: String = ""

    @SerializedName("post_Photos")
    var post_Photos: List<String> = ArrayList()

    @SerializedName("youtube_Id")
    var youtube_Id: String = ""

    @SerializedName("topic_Id")
    var topic_Id: Int = 0

    @SerializedName("product_Id")
    var product_Id: Int = 0

    @SerializedName("post_Status")
    var post_Status: Int = 0

    @SerializedName("rating_Id")
    var rating_Id: Int = 0
}
