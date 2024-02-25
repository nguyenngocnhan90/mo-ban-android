package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.Photo
import com.moban.model.data.post.Post
import com.moban.model.param.post.CreateCommentParam
import com.moban.model.param.post.CreatePostParam
import com.moban.model.param.post.EditPostParam
import com.moban.model.response.BaseListResponse
import com.moban.model.response.SimpleResponse
import com.moban.model.response.post.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by neal on 3/5/18.
 */
interface PostService {

    /**
     * Get all topics
     */
    @GET(ApiController.post + "topics")
    fun topics(): Call<ListTopicResponse>

    /**
     * Get filter topics
     */
    @GET(ApiController.post + "filter_topics")
    fun filterTopics(): Call<ListTopicResponse>

    /**
     * Get all posts
     */
    @GET(ApiController.post)
    fun posts(
        @Query("lowest_id") lowestId: Int?,
        @Query("topic_Id") topicId: Int
    ): Call<ListPostResponse>

    /**
     * Get favorite posts
     */
    @GET(ApiController.post + "favorites")
    fun favoritePosts(@Query("lowest_id") lowestId: Int?): Call<ListPostResponse>

    /**
     * Get pos by id
     */
    @GET(ApiController.post + "{id}")
    fun get(@Path("id") id: Int): Call<PostResponse>

    /**
     * Like a post
     */
    @POST(ApiController.post + "{id}/like")
    fun like(@Path("id") id: Int): Call<PostStatusResponse>

    /**
     * Unlike a post
     */
    @DELETE(ApiController.post + "{id}/unlike")
    fun unlike(@Path("id") id: Int): Call<PostStatusResponse>

    /**
     * Get list comments
     */
    @GET(ApiController.post + "{id}/comments")
    fun comments(
        @Path("id") id: Int,
        @Query("lowest_id") lowestId: Int?
    )
            : Call<ListCommentResponse>

    /**
     * Create a comment
     */
    @POST(ApiController.post + "{id}/comments")
    fun comment(
        @Path("id") id: Int,
        @Body param: CreateCommentParam
    )
            : Call<NewPostResponse>

    /**
     * Follow a post
     */
    @POST(ApiController.post + "{id}/follow")
    fun follow(@Path("id") id: Int): Call<PostResponse>

    /**
     * Unfollow a post
     */
    @DELETE(ApiController.post + "{id}/unfollow")
    fun unfollow(@Path("id") id: Int): Call<PostResponse>

    /**
     * Create a post
     */
    @POST(ApiController.post)
    fun post(@Body param: CreatePostParam): Call<PostResponse>

    /**
     * Edit a post
     */
    @PUT(ApiController.post + "{id}")
    fun update(@Path("id") id: Int, @Body param: EditPostParam): Call<PostResponse>

    /**
     * Delete a post
     */
    @DELETE(ApiController.post + "{id}")
    fun delete(@Path("id") id: Int): Call<SimpleResponse>

    /**
     * Share a post
     */
    @POST(ApiController.post + "{id}/share")
    fun trackShare(@Path("id") id: Int): Call<PostResponse>

    /**
     * Get list banners
     */
    @GET(ApiController.post + "banners")
    fun banners(): Call<BaseListResponse<Photo>>

    /**
     * Get all posts home
     */
    @GET(ApiController.post + "homes")
    fun homes(@Query("lowest_id") lowestId: Int?): Call<BaseListResponse<Post>>
}
