package com.moban.flow.rank

import android.content.Context
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.enum.PartnerFilterType
import com.moban.model.response.user.ListUsersResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by LenVo on 6/26/18.
 */
class TopPartnerPresenterIml : BaseMvpPresenter<ITopPartnerView>, ITopPartnerPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: ITopPartnerView? = null
    private var context: Context? = null

    override fun attachView(view: ITopPartnerView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadTopPartner(page: Int, partnerType: PartnerFilterType) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        userService?.topPartners(page, Constant.PAGE_SIZE_SEARCH, partnerType.value)?.enqueue(object : Callback<ListUsersResponse> {
            override fun onFailure(call: Call<ListUsersResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListUsersResponse>?, response: Response<ListUsersResponse>?) {
                val data = response?.body()?.data
                if (data != null) {
                    data.paging?.let { paging ->
                        val canLoadMore = page < paging.total
                        view?.bindingTopPartner(data, canLoadMore, partnerType)
                    }
                }
            }
        })
    }
}
