package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartBrand
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.param.linkmart.PurchaseProductParam
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by H. Len Vo on 9/5/18.
 */
interface LinkmartService {
    /**
     * Get all linkmart products
     */
    @GET(ApiController.linkmartProduct)
    fun allProducts(): Call<List<Linkmart>>

    /**
     * Get all linkmart products by category
     */
    @GET(ApiController.linkmartProduct)
    fun allProductsByCategory(@Query("category") category: Int,
                              @Query("orderby") orderby: String = "date"): Call<List<Linkmart>>

    /**
     * Get a linkmart products
     */
    @GET(ApiController.linkmartProduct + "{id}")
    fun get(@Path("id") id: Int): Call<Linkmart>

    /**
     * Get categories linkmart products
     */
    @GET(ApiController.linkmartProduct + "categories")
    fun categories(@Query("parent") parent: Int? = null, @Query("page") page: Int = 1, @Query("per_page") per_page: Int? = null,
                   @Query("hide_empty") hide_empty: Boolean = true): Call<List<LinkmartCategory>>

    /**
     * Get a category linkmart products
     */
    @GET(ApiController.linkmartProduct + "categories/{id}")
    fun category(@Path("id") id: Int): Call<LinkmartCategory>

    /**
     * Get categories linkmart products
     */
    @GET(ApiController.linkmartProduct + "brands")
    fun brands(): Call<List<LinkmartBrand>>

    /**
     * Get special linkmart products
     */
    @GET(ApiController.linkmartProduct)
    fun specials(@Query("on_sale") feature: Boolean = true,
                 @Query("status") status: String = "publish",
                 @Query("stock_status") stock_status: String = "instock",
                 @Query("category") category: Int? = null,
                 @Query("per_page") per_page: Int? = null,
                 @Query("page") page: Int = 1,
                 @Query("orderby") orderby: String = "date",
                 @Query("order") order: String = "desc"
    ): Call<List<Linkmart>>

    /**
     * Get newest linkmart products
     */
    @GET(ApiController.linkmartProduct)
    fun newest(@Query("status") status: String = "publish",
               @Query("stock_status") stock_status: String = "instock",
               @Query("category") category: Int? = null,
               @Query("per_page") per_page: Int? = null,
               @Query("page") page: Int = 1,
               @Query("order") order: String = "desc",
               @Query("orderby") orderby: String = "date"
    ): Call<List<Linkmart>>

    /**
     * Get popular linkmart products
     */
    @GET(ApiController.linkmartProduct)
    fun popular(@Query("featured") feature: Boolean = true,
                @Query("status") status: String = "publish",
                @Query("stock_status") stock_status: String = "instock",
                @Query("per_page") per_page: Int? = null,
                @Query("category") category: Int? = null,
                @Query("page") page: Int = 1
    ): Call<List<Linkmart>>


    /**
     * Get purchased linkmart products
     */
    @GET(ApiController.orderProduct)
    fun purchased(@Query("customer") customer: String, @Query("orderby") orderby: String = "date",
                  @Query("order") order: String = "desc"): Call<List<LinkmartOrder>>


    /**
     * purchase a linkmart product
     */
    @POST(ApiController.orderProduct)
    fun purchase(@Body param: PurchaseProductParam): Call<LinkmartOrder>

}
