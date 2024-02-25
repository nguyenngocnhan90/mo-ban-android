package com.moban.model.data.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.media.Photo
import com.moban.model.data.project.Project
import com.moban.model.data.user.User

/**
 * Created by thangnguyen on 12/18/17.
 */
class Post : BaseModel() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("user_Id")
    var user_Id: Int = 0

    @SerializedName("user_Profile_Name")
    var user_Profile_Name: String? = null

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("post_Content")
    var post_Content: String? = null

    @SerializedName("original_Id")
    var original_Id: Int = 0

    @SerializedName("created_Date")
    var created_Date: Double = 0.0

    @SerializedName("post_Status")
    var post_Status: Int = 0

    @SerializedName("post_User_Tag")
    var post_User_Tag: List<User> = ArrayList()

    @SerializedName("post_Product_Tags")
    var post_Product_Tags: List<Project> = ArrayList()

    @SerializedName("likes")
    var likes: Int = 0

    @SerializedName("comments")
    var comments: Int = 0

    @SerializedName("is_Liked")
    var is_Liked: Boolean = false

    @SerializedName("is_Followed")
    var is_Followed: Boolean = false

    @SerializedName("photos")
    var photos: List<Photo> = ArrayList()

    @SerializedName("video")
    var video: Video? = null

    @SerializedName("topic")
    var topic: Topic? = null

    @SerializedName("web_Url")
    var web_Url: String = ""

    var isExpanded: Boolean = false

    val youtubeId: String
        get() = video?.youtube_Id ?: ""

    val youtubeUrl: String
        get() {
            return if (youtubeId.isEmpty()) "" else ("https://www.youtube.com/watch?v=" + youtubeId)
        }

    @SerializedName("type")
    var type: Int = 0

    fun hasVideo(): Boolean {
        return youtubeId.isNotEmpty()
    }

    fun hasMedia(): Boolean {
        return hasVideo() || photos.isNotEmpty()
    }

    fun getFullContent(): String {
        var content = ""
        post_Content?.let {
            content = it.replace('\u2029', '\n').replace('\u2028', '\n')
        }
        return content
    }
}
