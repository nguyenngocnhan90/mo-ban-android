package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.user.User
import com.moban.model.response.BaseListResponse
import com.moban.model.response.user.LevelResponse
import com.moban.model.response.user.ListLevelGiftResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by LenVo on 3/30/19.
 */
interface LevelService {
    /**
     * Get Level
     */
    @GET(ApiController.levels + "{id}")
    fun get(@Path("id") id: Int): Call<LevelResponse>

    /**
     * Get gifts
     */
    @GET(ApiController.levels + "{id}/gifts")
    fun gifts(@Path("id") id: Int): Call<ListLevelGiftResponse>

    /**
     * Get top
     */
    @GET(ApiController.levels + "/top")
    fun topPartners(): Call<BaseListResponse<User>>
}
