package com.moban.flow.secondary.searchresult

import android.content.Context
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.param.SearchAdvanceParams
import com.moban.model.response.secondary.ListSecondaryHouseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.SecondaryHouseService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 5/7/18.
 */
class SearchSecondaryProjectResultPresenterIml : BaseMvpPresenter<ISearchSecondaryProjectResultView>, ISearchSecondaryProjectResultPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val projectService: SecondaryHouseService? = retrofit?.create(SecondaryHouseService::class.java)

    private var view: ISearchSecondaryProjectResultView? = null
    private var context: Context? = null

    override fun attachView(view: ISearchSecondaryProjectResultView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun searchProjectWithKeyword(page: Int, keyword: String) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.search(page, Constant.PAGE_SIZE_SEARCH, keyword)?.enqueue(object : Callback<ListSecondaryHouseResponse> {
            override fun onFailure(call: Call<ListSecondaryHouseResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListSecondaryHouseResponse>?, response: Response<ListSecondaryHouseResponse>?) {
                response?.body()?.let { it ->
                    it.data?.let {
                        var canLoadMore = false
                        it.paging?.let {paging ->
                            canLoadMore = page < paging.total
                        }

                        view?.bindingProject(it, canLoadMore)
                    }
                }
            }

        })
    }

    override fun searchAdvance(searchAdvanceParams: SearchAdvanceParams) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        var typesStr: String? = null
        searchAdvanceParams.types?.let {
            typesStr = it.joinToString(separator = ",")
        }
//        projectService?.searchAdvance(searchAdvanceParams.host, typesStr, searchAdvanceParams.minPrice, searchAdvanceParams.maxPrice,
//                searchAdvanceParams.minRate, searchAdvanceParams.maxRate, searchAdvanceParams.districtId, searchAdvanceParams.cityId)?.enqueue(object : Callback<ListProjectResponse> {
//            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
//                t.toString()
//            }
//
//            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {
//                response?.body()?.let {
//                    it.data?.let {
//                        view?.bindingProject(it, false)
//                    }
//                }
//            }
//
//        })
    }
}
