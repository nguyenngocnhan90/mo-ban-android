package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.response.PhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * Created by LenVo on 3/22/18.
 */
interface ImageService {
    /**
     * upload a image
     */
    @Multipart
    @POST(ApiController.images)
    fun upload(@Part image : MultipartBody.Part, @Part("name") name: RequestBody): Call<PhotoResponse>

    @GET
    fun download(@Url url: String): Call<ResponseBody>

    @POST(ApiController.images + "{id}" + "/save")
    fun trackSave(@Path("id") id: Int): Call<PhotoResponse>
}
