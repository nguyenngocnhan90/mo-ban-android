package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.constant.Constant
import com.moban.model.response.secondary.ListSecondaryHouseResponse
import com.moban.model.response.secondary.project.ListSecondaryProjectResponse
import com.moban.model.response.secondary.project.SecondaryProjectResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by lenvo on 2020-02-23.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface SecondaryProjectService {
    /**
     * Get all projects
     */
    @GET(ApiController.secondaryProduct)
    fun projects(@Query("pageIndex") page: Int, @Query("pageSize") pageSize: Int = Constant.PAGE_SIZE_SEARCH): Call<ListSecondaryProjectResponse>

    /**
     * Get a project
     */
    @GET(ApiController.secondaryProduct + "{id}")
    fun get(@Path("id") id: Int): Call<SecondaryProjectResponse>

    /**
     * Get all houses
     */
    @GET(ApiController.secondaryProduct + "{id}/houses")
    fun houses(@Path("id") id: Int): Call<ListSecondaryHouseResponse>
}
