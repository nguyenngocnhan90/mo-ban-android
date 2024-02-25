package com.moban.network.service

import com.moban.constant.ApiController
import com.moban.enum.DealFilter
import com.moban.model.param.linkmart.PayLinkmartParam
import com.moban.model.param.user.*
import com.moban.model.response.*
import com.moban.model.response.badge.BadgeResponse
import com.moban.model.response.badge.ListBadgeResponse
import com.moban.model.response.coin.ListLinkPointTransactionsResponse
import com.moban.model.response.deal.DealResponse
import com.moban.model.response.post.ListPostResponse
import com.moban.model.response.project.ListProjectResponse
import com.moban.model.response.user.*
import com.moban.model.response.coin.ListTransactionsResponse
import com.moban.model.response.userinfo.TransactionsResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by neal on 3/3/18.
 */
interface UserService {

    /**
     * Login
     */
    @POST(ApiController.user)
    fun login(@Body param: LoginParam): Call<LoginResponse>

    @POST(ApiController.user + "anonymous")
    fun loginAnonymous(): Call<LoginResponse>

    /**
     * Logout
     */
    @DELETE(ApiController.user)
    fun logout(): Call<Void>

    /**
     * Logout
     */
    @DELETE(ApiController.user + "delete")
    fun deleteAccount(): Call<SimpleResponse>

    /**
     * My profile
     */
    @GET(ApiController.user)
    fun profile(): Call<LoginResponse>

    /**
     * profile
     */
    @GET(ApiController.user + "{id}")
    fun user(@Path("id") id: Int): Call<LoginResponse>

    /**
     * Search
     */
    @GET(ApiController.user + "search")
    fun search(@Query("s") keyword: String): Call<ListUsersResponse>

    /**
     * badges
     */
    @GET(ApiController.user + "{id}/badges")
    fun myBadges(@Path("id") id: Int): Call<ListBadgeResponse>

    /**
     * Transaction
     */
    @GET(ApiController.user + "transactions/{id}")
    fun transaction(@Path("id") id: Int): Call<TransactionsResponse>

    /**
     * Salary
     */
    @GET(ApiController.user + "salary")
    fun salary(@Query("id") userId: Int, @Query("month") month: Int,
               @Query("year") year: Int): Call<SalaryResponse>

    /**
     * My Deals
     */
    @GET(ApiController.user + "{id}/deals")
    fun deals(@Path("id") id: Int,
              @Query("pageIndex") page: Int,
              @Query("deal_status") dealStatus: Int?,
              @Query("approve_status") approveStatus: Int?,
              @Query("filter") filter: Int = DealFilter.ALL.value): Call<ListDealsResponse>

    /**
     * Get Deal
     */
    @GET(ApiController.user + "deals/{id}")
    fun deal(@Path("id") id: Int): Call<DealResponse>

    /**
     * Missions
     */
    @GET(ApiController.user + "missions")
    fun missions(): Call<ListMissionResponse>

    /**
     * Mission detail
     */
    @GET(ApiController.user + "missions/{id}")
    fun mission(@Path("id") id: Int): Call<MissionResponse>

    /**
     * Change password
     */
    @POST(ApiController.user + "change_password")
    fun changePassword(@Body param: ChangePasswordParam): Call<SimpleResponse>

    /**
     * Badge Detail
     */
    @GET(ApiController.user + "badges/{id}")
    fun badge(@Path("id") id: Int): Call<BadgeResponse>

    /**
     * Get all favorite projects
     */
    @GET(ApiController.user + "favorite_products")
    fun favoriteProjects(@Query("pageIndex") page: Int, @Query("pageSize") pageSize: Int): Call<ListProjectResponse>

    /**
     * Get all favorite projects
     */
    @GET(ApiController.user + "top")
    fun topPartners(@Query("pageIndex") page: Int, @Query("pageSize") pageSize: Int, @Query("filter") filter: String): Call<ListUsersResponse>

    /**
     * Check in
     */
    @POST(ApiController.user + "checkin")
    fun checkin(@Body param: CheckinParam): Call<CheckInResponse>

    /**
     * Pay
     */
    @POST(ApiController.user + "pay")
    fun pay(@Body param: PayLinkmartParam): Call<ApiResponse>

    /**
     * Reset Password
     */
    @POST(ApiController.user + "forgot_password")
    fun resetPassword(@Body param: ResetPasswordParam): Call<SimpleResponse>

    /**
     * Reset Password
     */
    @POST(ApiController.user + "recover_password")
    fun recoverPassword(@Body param: RecoverPasswordParam): Call<SimpleResponse>

    /**
     * Get my posts
     */
    @GET(ApiController.user + "{id}/posts")
    fun myPosts(@Path("id") id: Int, @Query("lowest_id") lowestId: Int?): Call<ListPostResponse>

    /**
     * Transfer Coin
     */
    @POST(ApiController.user + "transfer_coin")
    fun transferCoin(@Body param: TransferCoinParam): Call<SimpleResponse>

    /**
     * Get one time token
     */
    @GET(ApiController.user + "onetime_token")
    fun oneTimeToken(): Call<SimpleResponse>

    /**
     * Get my rewards
     */
    @GET(ApiController.user + "rewards")
    fun myRewards(@Query("pageIndex") page: Int?): Call<ListLevelGiftResponse>

    /**
     * Get gifts
     */
    @GET(ApiController.user + "{id}/gifts")
    fun gifts(@Path("id") id: Int): Call<ListLevelGiftResponse>

    /**
     * Buy a gift
     */
    @PUT(ApiController.user + "use_gift")
    fun useGift(@Body param: UseGiftParam): Call<ListLevelGiftResponse>

    /**
     * Get OTP
     */
    @POST(ApiController.user + "get_otp")
    fun getOTP(@Body param: GetOTPParam): Call<SimpleResponse>

    /**
     * Get all transactions
     */
    @GET(ApiController.user + "{id}/transactions")
    fun transactions(@Path("id") id: Int, @Query("pageIndex") page: Int?): Call<ListTransactionsResponse>

    /**
     * Get all linkpoints
     */
    @GET(ApiController.user + "{id}/linkpoints")
    fun linkpoints(@Path("id") id: Int, @Query("pageIndex") page: Int?): Call<ListLinkPointTransactionsResponse>

}
