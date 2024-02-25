package com.moban.flow.linkmart.detail

import android.content.Context
import android.util.Base64
import com.moban.LHApplication
import com.moban.helper.LocalStorage
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.model.param.linkmart.*
import com.moban.model.param.user.UseGiftParam
import com.moban.model.response.ApiResponse
import com.moban.model.response.user.LevelGiftResponse
import com.moban.model.response.user.ListLevelGiftResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.GiftUserService
import com.moban.network.service.UserService
import com.moban.util.Decrypter
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LinkmartPresenterIml: BaseMvpPresenter<ILinkmartDetailView>, ILinkmartPresenter {
    private val retrofitNet: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofitNet?.create(UserService::class.java)
    private val giftService = retrofitNet?.create(GiftUserService::class.java)

    private var view: ILinkmartDetailView? = null
    private var context: Context? = null

    override fun attachView(view: ILinkmartDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadLinkmartDetail(linkmartId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.get(linkmartId)?.enqueue(object : Callback<Linkmart> {
            override fun onFailure(call: Call<Linkmart>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<Linkmart>?, response: Response<Linkmart>?) {
                response?.body()?.let {
                    view?.bindingProductDetail(it)
                }
            }
        })
    }

    override fun purchase(linkmart: Linkmart) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        pay(linkmart)
    }

    private fun pay(linkmart: Linkmart) {
        val user = LocalStorage.user() ?: return
        val linkmartToken = user.linkmart_token
        val password = "AKIAJVXEMMY7I6OOULBA:$linkmartToken"
        val transactionId = Base64.encodeToString(Decrypter.getTransactionId(password),
                Base64.NO_WRAP)
        val param = PayLinkmartParam()
        param.transaction_id = transactionId
        param.product_id = linkmart.id
        param.product_name = linkmart.name
        param.lhc = linkmart.officalPrice()
        linkmart.categories?.let { it ->
            param.product_categories = it.map { LinkmartCategoryParam(it) }
        }

        userService?.pay(param)?.enqueue(object : Callback<ApiResponse> {
            override fun onFailure(call: Call<ApiResponse>?, t: Throwable?) {
                view?.bindingPurchaseFailed(null)
            }

            override fun onResponse(call: Call<ApiResponse>?, response: Response<ApiResponse>?) {
                response?.let { it ->
                    var message: String? = null
                    if(it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                doPurchaseLinkmart(linkmart)
                                return
                            }
                            message = it.error
                        }
                    }
                    view?.bindingPurchaseFailed(message)
                }
            }
        })
    }

    private fun doPurchaseLinkmart(linkmart: Linkmart) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        val user = LocalStorage.user()
        if (user == null || user.linkmart_customer_id.isNullOrBlank()) {
            return
        }

        val param = PurchaseProductParam()
        param.set_paid = true
        param.customer_id = user.linkmart_customer_id!!

        val billing = BillingParam()
        billing.first_name = user.name
        billing.last_name = user.username
        billing.address_1 = user.address
        billing.email = user.email
        billing.phone = user.phone
        param.billing = billing

        val lineItem = LineItemParam()
        lineItem.product_id = linkmart.id
        lineItem.quantity = 1
        param.line_items.add(lineItem)

        LHApplication.instance.getLinkmartService()?.purchase(param)?.enqueue(object : Callback<LinkmartOrder> {
            override fun onFailure(call: Call<LinkmartOrder>?, t: Throwable?) {
                view?.bindingPurchaseFailed(null)
            }

            override fun onResponse(call: Call<LinkmartOrder>?, response: Response<LinkmartOrder>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingProductPurchased(it)
                            return
                        }
                    }
                    view?.bindingPurchaseFailed(null)

                }
            }
        })
    }

    override fun buyGiftLinkmart(gift: LevelGift) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        val user = LocalStorage.user()
        if (user == null || user.linkmart_customer_id.isNullOrBlank()) {
            return
        }

        val param = PurchaseProductParam()
        param.set_paid = true
        param.customer_id = user.linkmart_customer_id!!

        val billing = BillingParam()
        billing.first_name = user.name
        billing.last_name = user.username
        billing.address_1 = user.address
        billing.email = user.email
        billing.phone = user.phone
        param.billing = billing

        val lineItem = LineItemParam()
        lineItem.product_id = gift.linkmart_Id
        lineItem.quantity = 1
        param.line_items.add(lineItem)

        LHApplication.instance.getLinkmartService()?.purchase(param)?.enqueue(object : Callback<LinkmartOrder> {
            override fun onFailure(call: Call<LinkmartOrder>?, t: Throwable?) {
                view?.purchaseFailed()
            }

            override fun onResponse(call: Call<LinkmartOrder>?, response: Response<LinkmartOrder>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.purchaseCompleted()
                            return
                        }
                    }

                    view?.purchaseFailed()
                }
            }
        })
    }

    override fun markUseGift(gift: LevelGift) {
        val user = LocalStorage.user() ?: return
        val param = UseGiftParam()
        param.gift_level_id = gift.gift_level_Id
        param.user_id = user.id
        param.gift_Id = gift.gift_Id

        userService?.useGift(param)?.enqueue(object : Callback<ListLevelGiftResponse> {
            override fun onFailure(call: Call<ListLevelGiftResponse>?, t: Throwable?) {
                view?.purchaseFailed()
            }

            override fun onResponse(call: Call<ListLevelGiftResponse>?, response: Response<ListLevelGiftResponse>?) {
                response?.let { it ->
                    var error: String? = null
                    if(it.isSuccessful) {
                        it.body()?.let { response ->
                            if (response.success) {
                                response.data?.let {
                                    view?.markUseCompleted(it.list, gift)
                                    return
                                }
                            }
                            error = response.error
                        }
                    }
                    view?.purchaseFailed(error)
                }
            }
        })
    }

    override fun getLevelGift(giftId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        giftService?.get(giftId)?.enqueue(object : Callback<LevelGiftResponse> {
            override fun onFailure(call: Call<LevelGiftResponse>?, t: Throwable?) {
                view?.loadLevelGiftFailed()
            }

            override fun onResponse(call: Call<LevelGiftResponse>?, response: Response<LevelGiftResponse>?) {
                val levelGift = response?.body()?.data
                if (levelGift == null) {
                    view?.loadLevelGiftFailed()
                    return
                }

                view?.bindingLevelGift(levelGift)

            }
        })
    }
}
