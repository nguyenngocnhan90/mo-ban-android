package com.moban.flow.searchpartner

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.user.ListUsersResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by LenVo on 5/11/18.
 */
class SearchPartnerPresenterIml : BaseMvpPresenter<ISearchPartnerView>, ISearchPartnerPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: ISearchPartnerView? = null
    private var context: Context? = null

    override fun attachView(view: ISearchPartnerView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun searchPartner(keyword: String) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.search(keyword)?.enqueue(object : Callback<ListUsersResponse> {
            override fun onFailure(call: Call<ListUsersResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListUsersResponse>?, response: Response<ListUsersResponse>?) {
                response?.body()?.data?.let {
                    view?.bindingSearchPartner(it.list)
                }
            }
        })
    }
}
