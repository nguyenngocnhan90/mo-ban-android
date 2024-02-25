package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by LenVo on 7/26/18.
 */
interface CityService {
    /**
     * Get Cities
     */
    @GET(ApiController.cities)
    fun cities(): Call<List<City>>

    /**
     * Get District
     */
    @GET(ApiController.cities + "{id}/districts")
    fun districts(@Path("id") id: Int): Call<List<District>>
}
