package com.moban.flow.projects.searchprojectresult

import android.content.Context
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.param.SearchAdvanceParams
import com.moban.model.response.project.ListProjectResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 5/7/18.
 */
class SearchProjectResultPresenterIml : BaseMvpPresenter<ISearchProjectResultView>, ISearchProjectResultPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)

    private var view: ISearchProjectResultView? = null
    private var context: Context? = null

    override fun attachView(view: ISearchProjectResultView) {
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

        projectService?.search(page, Constant.PAGE_SIZE_SEARCH, keyword)?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {
                response?.body()?.let {
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
        projectService?.searchAdvance(searchAdvanceParams.host, typesStr, searchAdvanceParams.minPrice, searchAdvanceParams.maxPrice,
                searchAdvanceParams.minRate, searchAdvanceParams.maxRate, searchAdvanceParams.districtId, searchAdvanceParams.cityId)?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        view?.bindingProject(it, false)
                    }
                }
            }

        })
    }
}
