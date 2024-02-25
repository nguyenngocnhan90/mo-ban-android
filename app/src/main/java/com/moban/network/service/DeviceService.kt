package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.param.user.UpdateDeviceTokenParam
import com.moban.model.response.ForceResponse
import com.moban.model.response.post.PostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Created by neal on 3/25/18.
 */
interface DeviceService {
    /**
     * Update device token
     */
    @PUT(ApiController.devices)
    fun updateDeviceToken(@Body param: UpdateDeviceTokenParam): Call<PostResponse>

    /**
     * Get latest version
     */
    @GET(ApiController.devices)
    fun getLatestVersion(@Query("app_version") app_version: Int?, @Query("platform") platform: String? = "android"): Call<ForceResponse>
}
