package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.document.Document
import com.moban.model.data.homedeal.HomeDeal
import com.moban.model.data.homedeal.HomeDealDetail
import com.moban.model.response.BaseListResponse
import com.moban.model.response.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by H. Len Vo on 5/25/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface HomeService {

    /**
     * Get list home deal
     */
    @GET(ApiController.home  + "deals")
    fun deals(): Call<BaseListResponse<HomeDeal>>


    /**
     * Get home deal detail
     */
    @GET(ApiController.home  + "deals/{type}")
    fun dealDetail(@Path("type") typeId: String): Call<BaseResponse<HomeDealDetail>>

    /**
     * Get home deal detail
     */
    @GET(ApiController.home  + "deals/{type}/deals")
    fun getListDeals(@Path("type") typeId: String, @Query("pageIndex") page: Int): Call<BaseListResponse<Document>>
}
