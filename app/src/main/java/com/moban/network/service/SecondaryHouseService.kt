package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.constant.Constant
import com.moban.model.param.NewSecondaryProjectParam
import com.moban.model.response.SimpleResponse
import com.moban.model.response.secondary.ListSecondaryHouseResponse
import com.moban.model.response.secondary.SecondaryHouseResponse
import retrofit2.Call
import retrofit2.http.*

interface SecondaryHouseService {
    /**
     * Get all projects
     */
    @GET(ApiController.house + "sell")
    fun projects(@Query("pageIndex") page: Int, @Query("pageSize") pageSize: Int = Constant.PAGE_SIZE_SEARCH): Call<ListSecondaryHouseResponse>

    /**
     * Mew a project
     */
    @POST(ApiController.house + "addhouse")
    fun create(@Body param: NewSecondaryProjectParam): Call<SimpleResponse>

    /**
     * Get all highlight projects
     */
    @GET(ApiController.house + "highlights")
    fun highlight(): Call<ListSecondaryHouseResponse>

    /**
     * Search projects
     */
    @GET(ApiController.house + "searchSuggestions")
    fun search(@Query("keyword") keyword: String): Call<ListSecondaryHouseResponse>

    /**
     * Search projects
     */
    @POST(ApiController.house + "find/{id}/request_contact")
    fun requestContact(@Path("id") id: Int): Call<SecondaryHouseResponse>

    /**
     * Search projects
     */
    @GET(ApiController.house + "find/{id}")
    fun find(@Path("id") id: Int): Call<SecondaryHouseResponse>

    /**
     * Search projects
     */
    @GET(ApiController.house + "search")
    fun search(@Query("pageIndex") page: Int, @Query("pageSize") pageSize: Int, @Query("keyword") keyword: String): Call<ListSecondaryHouseResponse>
}
