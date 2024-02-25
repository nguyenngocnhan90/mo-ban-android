package com.moban.network.service

import com.moban.model.response.secondary.constant.ListSecondaryAttributeResponse
import com.moban.model.response.secondary.constant.ListSecondaryBasicConstantResponse
import com.moban.model.response.secondary.constant.ListSecondaryHouseTypeResponse
import com.moban.model.response.secondary.constant.ListSecondaryProjectBaseResponse
import retrofit2.Call
import retrofit2.http.GET

interface SecondaryConstantService {
    /**
     * Get all attributeType
     */
    @GET("attributeType")
    fun attributeType(): Call<ListSecondaryAttributeResponse>

    /**
     * Get all products
     */
    @GET("products")
    fun products(): Call<ListSecondaryProjectBaseResponse>

    /**
     * Get all housetypes
     */
    @GET("housetypes")
    fun housetypes(): Call<ListSecondaryHouseTypeResponse>

    /**
     * Get all targettypeshouse
     */
    @GET("targettypeshouse")
    fun targettypeshouse(): Call<ListSecondaryBasicConstantResponse>


    /**
     * Get all unitprice
     */
    @GET("unitprice")
    fun unitprice(): Call<ListSecondaryBasicConstantResponse>


    /**
     * Get all unitagent
     */
    @GET("unitagent")
    fun unitagent(): Call<ListSecondaryBasicConstantResponse>


    /**
     * Get all directions
     */
    @GET("directions")
    fun directions(): Call<ListSecondaryBasicConstantResponse>

}
