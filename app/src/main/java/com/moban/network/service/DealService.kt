package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.param.NewReservationParam
import com.moban.model.response.SimpleResponse
import com.moban.model.response.chart.ChartResponse
import com.moban.model.response.deal.DealResponse
import com.moban.model.response.user.ListDealsResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by LenVo on 4/6/18.
 */
interface DealService {
    /**
     * Get Deal
     */
    @GET(ApiController.deals + "{id}")
    fun deals(@Path("id") id: Int): Call<DealResponse>

    @GET(ApiController.deals + "chart")
    fun chart(): Call<ChartResponse>

    @GET(ApiController.deals + "today?pageSize=20")
    fun today(@Query("pageIndex") page: Int): Call<ListDealsResponse>

    @POST(ApiController.deals)
    fun new(@Body param: NewReservationParam) : Call<DealResponse>

    @POST(ApiController.deals + "booking")
    fun newSimple(@Body param: NewReservationParam) : Call<DealResponse>

    @PUT(ApiController.deals)
    fun update(@Body param: NewReservationParam) : Call<DealResponse>

    @DELETE(ApiController.deals + "{id}")
    fun cancel(@Path("id") id: Int): Call<SimpleResponse>
}
