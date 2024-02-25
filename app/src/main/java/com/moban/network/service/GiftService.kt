package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.response.gift.ListGiftsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by LenVo on 4/11/18.
 */
interface GiftService {
    /**
     * Get gifts
     */
    @GET(ApiController.gifts)
    fun gifts(@Query("pageIndex") page: Int): Call<ListGiftsResponse>
}
