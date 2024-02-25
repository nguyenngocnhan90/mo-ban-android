package com.moban.flow.reward.partner

import android.content.Context
import com.moban.LHApplication
import com.moban.helper.LocalStorage
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.model.param.linkmart.BillingParam
import com.moban.model.param.linkmart.LineItemParam
import com.moban.model.param.linkmart.PurchaseProductParam
import com.moban.model.param.user.UseGiftParam
import com.moban.model.response.user.ListLevelGiftResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RewardPartnerPresenterIml: BaseMvpPresenter<IRewardPartnerView>, IRewardPartnerPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    val userService = retrofit?.create(UserService::class.java)

    private var view: IRewardPartnerView? = null
    private var context: Context? = null

    override fun attachView(view: IRewardPartnerView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun buyLinkmart(gift: LevelGift) {
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
}
