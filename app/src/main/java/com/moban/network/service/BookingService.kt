package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.booking.BookingParam
import com.moban.model.param.UpdateInvoiceParam
import com.moban.model.response.project.ProjectBookingResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by LenVo on 3/19/18.
 */
interface BookingService {

    /**
     * Get a booking
     */
    @GET(ApiController.bookings + "{id}")
    fun booking(@Path("id") id: Int): Call<ProjectBookingResponse>

    /**
     * Update booking Invoice
     */
    @PUT(ApiController.bookings)
    fun updateInvoice(@Body param: UpdateInvoiceParam): Call<ProjectBookingResponse>

    /**
     * New booking
     */
    @POST(ApiController.bookings)
    fun new(@Body param: BookingParam): Call<ProjectBookingResponse>


}
