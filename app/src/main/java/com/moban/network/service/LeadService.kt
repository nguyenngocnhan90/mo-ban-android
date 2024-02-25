package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.param.user.RegisterParam
import com.moban.model.response.ListStringDataResponse
import com.moban.model.response.LoginResponse
import com.moban.model.response.user.ListUsersResponse
import com.moban.model.response.user.OtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by LenVo on 3/30/19.
 */
interface LeadService {

    /**
     * Get sources
     */
    @GET(ApiController.leads + "search")
    fun users(@Query("s") keyword: String): Call<ListUsersResponse>

    /**
     * Get sources
     */
    @GET(ApiController.leads + "sources")
    fun sources(): Call<ListStringDataResponse>

    /**
     * Get directors
     */
    @GET(ApiController.leads + "giamdoc")
    fun directors(): Call<ListUsersResponse>

    /**
     * Get otp
     */
    @POST(ApiController.leads + "otp")
    fun getOTP(@Body param: RegisterParam): Call<OtpResponse>

    /**
     * Register
     */
    @POST(ApiController.leads + "register")
    fun register(@Body param: RegisterParam): Call<LoginResponse>
}
