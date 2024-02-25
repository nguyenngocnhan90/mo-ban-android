package com.moban.flow.linkmart

import android.content.Context
import com.moban.LHApplication
import com.moban.enum.LinkmartMainCategory
import com.moban.helper.LocalStorage
import com.moban.model.data.linkmart.Linkmart
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.linkmart.LinkmartOrder
import com.moban.model.data.user.LevelGift
import com.moban.model.param.linkmart.BillingParam
import com.moban.model.param.linkmart.LineItemParam
import com.moban.model.param.linkmart.PurchaseProductParam
import com.moban.model.param.user.UseGiftParam
import com.moban.model.response.LoginResponse
import com.moban.model.response.user.ListLevelGiftResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LinkmartPresenterIml : BaseMvpPresenter<ILinkmartView>, ILinkmartPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: ILinkmartView? = null
    private var context: Context? = null

    override fun attachView(view: ILinkmartView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadCategories(parentId: Int, perPage: Int?) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        var parent: Int? = null
        if (parentId > 0) {
            parent = parentId
        }
        LHApplication.instance.getLinkmartService()?.categories(parent = parent, per_page = perPage)?.enqueue(object : Callback<List<LinkmartCategory>> {
            override fun onFailure(call: Call<List<LinkmartCategory>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<LinkmartCategory>>?, response: Response<List<LinkmartCategory>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { categories ->
                            val result = categories.sortedBy { cate -> cate.menu_order }
                            when(parentId) {
                                LinkmartMainCategory.linkMart.value -> LHApplication.instance.lhCache.linkmartCategories = result
                                LinkmartMainCategory.linkHub.value -> LHApplication.instance.lhCache.linkHubCategories = result
                                LinkmartMainCategory.linkBook.value -> LHApplication.instance.lhCache.linkBookCategories = result
                            }
                            view?.bindingCategories(result)
                        }
                    }
                }
            }
        })
    }

    override fun loadSpecialDeal(cateId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.specials(category = cateId, per_page = 5)?.enqueue(object : Callback<List<Linkmart>> {
            override fun onFailure(call: Call<List<Linkmart>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Linkmart>>?, response: Response<List<Linkmart>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingSpecials(it)
                        }
                    }
                }
            }
        })
    }

    override fun loadNewestDeal(cateId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.newest(category = cateId, per_page = 5)?.enqueue(object : Callback<List<Linkmart>> {
            override fun onFailure(call: Call<List<Linkmart>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Linkmart>>?, response: Response<List<Linkmart>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingNewest(it)
                        }
                    }
                }
            }
        })
    }

    override fun loadPopularDeal(cateId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.popular(category = cateId, per_page = 5)?.enqueue(object : Callback<List<Linkmart>> {
            override fun onFailure(call: Call<List<Linkmart>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Linkmart>>?, response: Response<List<Linkmart>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingPopular(it)
                        }
                    }
                }
            }
        })
    }

    override fun loadPurchased(page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        if (LocalStorage.user() == null || LocalStorage.user()?.linkmart_customer_id.isNullOrBlank()) {
            return
        }

        val customerId = LocalStorage.user()?.linkmart_customer_id!!
        LHApplication.instance.getLinkmartService()?.purchased(customerId)?.enqueue(object : Callback<List<LinkmartOrder>> {
            override fun onFailure(call: Call<List<LinkmartOrder>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<LinkmartOrder>>?, response: Response<List<LinkmartOrder>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingOrders(it)
                        }
                    }
                }
            }
        })
    }

    override fun loadProfile() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.profile()?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.body()?.data?.let {
                    LocalStorage.saveUser(it)
                    view?.bindingCurrentLhc()
                }
            }
        })
    }

    override fun loadReward(page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        if (LocalStorage.user() == null) {
            return
        }

        userService?.myRewards(page)?.enqueue(object : Callback<ListLevelGiftResponse> {
            override fun onFailure(call: Call<ListLevelGiftResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListLevelGiftResponse>?, response: Response<ListLevelGiftResponse>?) {
                response?.body()?.let { it ->
                    it.data?.let {
                        view?.bindingRewards(it.list)
                    }
                }
            }
        })
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
