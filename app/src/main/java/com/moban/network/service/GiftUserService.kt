package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.response.user.LevelGiftResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by lenvo on 2020-03-01.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface GiftUserService {
    /**
     * Get gifts
     */
    @GET(ApiController.gift_users  + "{id}")
    fun get(@Path("id") id: Int): Call<LevelGiftResponse>
}
