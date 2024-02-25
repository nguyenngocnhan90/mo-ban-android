package com.moban.model.param.post

import com.google.gson.annotations.SerializedName

/**
 * Created by LenVo on 8/26/18.
 */
class EditPostParam {
    var id: Int = 0

    @SerializedName("post_Content")
    var post_Content: String = ""

    @SerializedName("post_Photos")
    var post_Photos: List<String> = ArrayList()

    @SerializedName("youtube_Id")
    var youtube_Id: String = ""

    @SerializedName("topic_Id")
    var topic_Id: Int = 0
}
