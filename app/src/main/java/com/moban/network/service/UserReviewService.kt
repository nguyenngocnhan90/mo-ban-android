package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.popup.Review
import com.moban.model.param.review.UserReviewParam
import com.moban.model.response.review.ReviewResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserReviewService {
    /**
     * get a review
     */
    @GET(ApiController.review + "{id}")
    fun get(@Path("id") id: Int): Call<Review>

    /**
     * submit review
     */
    @POST(ApiController.review)
    fun submitReview(@Body param: UserReviewParam): Call<ReviewResponse>
}
