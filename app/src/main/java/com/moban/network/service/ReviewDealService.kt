package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.param.user.NewDealReviewParam
import com.moban.model.response.deal.ReviewDealResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by LenVo on 4/6/19.
 */
interface ReviewDealService {
    @POST(ApiController.dealsReview)
    fun new(@Body param: NewDealReviewParam) : Call<ReviewDealResponse>
}
