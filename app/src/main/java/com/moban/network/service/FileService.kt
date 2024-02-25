package com.moban.network.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import okhttp3.ResponseBody



/**
 * Created by LenVo on 3/5/19.
 */
interface FileService {

    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>
}
