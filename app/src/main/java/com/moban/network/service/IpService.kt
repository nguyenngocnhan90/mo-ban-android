package com.moban.network.service

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by LenVo on 7/30/18.
 */
interface IpService {

    @GET(".")
    fun externalIp():Call<String>
}
