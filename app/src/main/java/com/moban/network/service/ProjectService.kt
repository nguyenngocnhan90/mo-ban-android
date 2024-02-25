package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.constant.Constant
import com.moban.enum.SortType
import com.moban.model.data.deal.Promotion
import com.moban.model.data.document.Document
import com.moban.model.data.project.Project
import com.moban.model.data.project.ProjectActivity
import com.moban.model.data.project.ProjectRating
import com.moban.model.data.project.SalePackage
import com.moban.model.param.project.JoinParam
import com.moban.model.param.project.ProjectRatingParam
import com.moban.model.response.ApiResponse
import com.moban.model.response.BaseListResponse
import com.moban.model.response.BaseResponse
import com.moban.model.response.ListStringResponse
import com.moban.model.response.document.DocumentGroupResponse
import com.moban.model.response.post.ListPostResponse
import com.moban.model.response.project.*
import com.moban.model.response.user.ListDealsResponse
import com.moban.model.response.user.ListInterestsResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by LenVo on 3/7/18.
 */
interface ProjectService {
    /**
     * Get all projects
     */
    @GET(ApiController.product)
    fun projects(
        @Query("pageIndex") page: Int,
        @Query("pageSize") pageSize: Int = Constant.PAGE_SIZE_SEARCH,
        @Query("sort") sort: String = SortType.RATE_ASC.type,
        @Query("type") type: Int? = null
    ): Call<BaseListResponse<Project>>

    /**
     * Get all projects
     */
    @GET(ApiController.product + "highlights")
    fun highlight(): Call<ListProjectResponse>

    /**
     * Get all projects
     */
    @GET(ApiController.product + "viewed")
    fun viewed(): Call<ListProjectResponse>

    /**
     * Search projects
     */
    @GET(ApiController.product + "search")
    fun search(
        @Query("pageIndex") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("s") key: String
    ): Call<ListProjectResponse>

    /**
     * Get a project
     */
    @GET(ApiController.product + "{id}")
    fun project(@Path("id") id: Int): Call<ProjectResponse>

    /**
     * Get all apartments
     */
    @GET(ApiController.product + "{id}/flats")
    fun apartments(@Path("id") id: Int, @Query("Block_Id") blockId: Int): Call<BlockResponse>

    /**
     * Get a apartment
     */
    @GET(ApiController.product + "flats/{id}")
    fun apartment(@Path("id") id: Int): Call<ApartmentResponse>

    /**
     * Get list project booking
     */
    @GET(ApiController.product + "{id}/bookings")
    fun bookings(
        @Path("id") id: Int, @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int = 20
    ): Call<ListProjectBookingResponse>

    /**
     * Get list deals
     */
    @GET(ApiController.product + "{id}/deals")
    fun deals(
        @Path("id") id: Int,
        @Query("deal_status") dealStatus: Int?,
        @Query("approve_status") approveStatus: Int?
    ): Call<ListDealsResponse>

    /**
     * Get list interests
     */
    @GET(ApiController.product + "{id}/interests")
    fun interests(@Path("id") id: Int): Call<ListInterestsResponse>


    /**
     * Set favorite
     */
    @POST(ApiController.product + "{id}/favorite")
    fun favorite(@Path("id") id: Int): Call<ProjectFavoriteResponse>

    /**
     * Set unfavorite
     */
    @DELETE(ApiController.product + "{id}/unfavorite")
    fun unFavorite(@Path("id") id: Int): Call<ProjectFavoriteResponse>

    /**
     * Get list saleKits
     */
    @GET(ApiController.product + "{id}/documents")
    fun saleKits(@Path("id") id: Int): Call<DocumentGroupResponse>

    /**
     * Get list policy
     */
    @GET(ApiController.product + "{id}/policies")
    fun policies(@Path("id") id: Int): Call<DocumentGroupResponse>

    /**
     * Get all posts
     */
    @GET(ApiController.product + "{id}/posts")
    fun posts(
        @Path("id") id: Int,
        @Query("lowest_id") lowestId: Int?,
        @Query("pageSize") pageSize: Int = 10
    ): Call<ListPostResponse>

    /**
     * Advance Search projects
     */
    @GET(ApiController.product + "searchadvance")
    fun searchAdvance(
        @Query("host") host: String?, @Query("type_id") type_id: String?,
        @Query("min_price") min_price: Long, @Query("max_price") max_price: Long,
        @Query("min_rate") min_rate: Int, @Query("max_rate") max_rate: Int,
        @Query("district") district: Int?, @Query("city") city: Int?
    ): Call<ListProjectResponse>

    /**
     * Get all hosts project
     */
    @GET(ApiController.product + "hosts")
    fun hosts(): Call<ListStringResponse>

    /**
     * Get all topPolicies
     */
    @GET(ApiController.product + "documents/index")
    fun topPolicies(): Call<DocumentGroupResponse>

    /**
     * Get all F1
     */
    @GET(ApiController.product + "highlights/f1")
    fun f1Highlights(
        @Query("pageIndex") page: Int = 1,
        @Query("pageSize") pageSize: Int = Constant.PAGE_SIZE_SEARCH,
        @Query("sort") sort: String = SortType.RATE_ASC.type,
        @Query("type") type: Int? = null
    ): Call<BaseListResponse<Project>>

    /**
     * Get all F2
     */
    @GET(ApiController.product + "highlights/f2")
    fun f2Highlights(
        @Query("pageIndex") page: Int = 1,
        @Query("pageSize") pageSize: Int = Constant.PAGE_SIZE_SEARCH,
        @Query("sort") sort: String = SortType.RATE_ASC.type,
        @Query("type") type: Int? = null
    ): Call<BaseListResponse<Project>>

    /**
     * Get list deals
     */
    @GET(ApiController.product + "{id}/home_deals")
    fun getListDeals(
        @Path("id") id: Int,
        @Query("pageIndex") page: Int
    ): Call<BaseListResponse<Document>>

    /**
     * Get list policies
     */
    @GET(ApiController.product + "{id}/special_deals")
    fun special_deals(@Path("id") id: Int): Call<BaseListResponse<Document>>

    /**
     * Get all related_products
     */
    @GET(ApiController.product + "{id}/related_products")
    fun related_products(@Path("id") id: Int): Call<BaseListResponse<Project>>

    /**
     * Get all sale package
     */
    @GET(ApiController.product + "{id}/subscribers")
    fun subscribers(@Path("id") id: Int): Call<BaseListResponse<SalePackage>>

    /**
     * Get all recent activity
     */
    @GET(ApiController.product + "{id}/recent_activities")
    fun recent_activities(@Path("id") id: Int): Call<BaseListResponse<ProjectActivity>>


    /**
     * Like a post
     */
    @POST(ApiController.product + "{id}/join")
    fun join(@Path("id") id: Int, @Body param: JoinParam): Call<ApiResponse>

    /**
     * Get list review post
     */
    @GET(ApiController.product + "{id}/reviewed_posts")
    fun reviewed_posts(
        @Path("id") id: Int,
        @Query("lowest_id") lowestId: Int?,
        @Query("pageSize") pageSize: Int = 10
    ): Call<ListPostResponse>

    /**
     * Get all promotions
     */
    @GET(ApiController.product + "{id}/promotions")
    fun promotions(@Path("id") id: Int): Call<BaseListResponse<Promotion>>

    /**
     * Submit new ratings
     */
    @POST(ApiController.product + "{id}/ratings")
    fun addRating(
        @Path("id") id: Int,
        @Body param: ProjectRatingParam
    ): Call<BaseResponse<ProjectRating>>
}
