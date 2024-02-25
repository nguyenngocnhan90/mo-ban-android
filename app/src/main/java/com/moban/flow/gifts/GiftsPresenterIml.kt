package com.moban.flow.gifts

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.gift.ListGiftsResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.GiftService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 4/11/18.
 */
class GiftsPresenterIml : BaseMvpPresenter<IGiftsView>, IGiftsPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val giftService = retrofit?.create(GiftService::class.java)

    private var view: IGiftsView? = null
    private var context: Context? = null

    override fun attachView(view: IGiftsView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadGifts(page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        giftService?.gifts(page)?.enqueue(object : Callback<ListGiftsResponse> {
            override fun onFailure(call: Call<ListGiftsResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListGiftsResponse>?, response: Response<ListGiftsResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        var canLoadMore = false
                        it.paging?.let {paging ->
                            canLoadMore = page < paging.total
                        }

                        view?.bindingGifts(it, canLoadMore)
                    }
                }
            }
        })
    }

}
