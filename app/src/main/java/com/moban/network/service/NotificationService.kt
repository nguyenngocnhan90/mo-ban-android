package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.response.SimpleResponse
import com.moban.model.response.notification.ListNotificationResponse
import com.moban.model.response.notification.NotificationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by LenVo on 5/11/18.
 */
interface NotificationService {
    /**
     * Get all transactions
     */
    @GET(ApiController.notification)
    fun notifications(@Query("pageIndex") page: Int?): Call<ListNotificationResponse>

    /**
     * Get a transactions
     */
    @GET(ApiController.notification + "{id}")
    fun notification(@Path("id") id: Int): Call<NotificationResponse>

    /**
     * Mark as read a notification
     */
    @POST(ApiController.notification + "{id}")
    fun markAsRead(@Path("id") id: Int): Call<SimpleResponse>
}
