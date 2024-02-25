package com.moban.flow.secondary.search

import android.content.Context
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.response.secondary.ListSecondaryHouseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.SecondaryHouseService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 7/22/18.
 */
class SearchSecondaryProjectPresenterIml : BaseMvpPresenter<ISearchSecondaryProjectView>, ISearchSecondaryProjectPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val projectService: SecondaryHouseService? = retrofit?.create(SecondaryHouseService::class.java)

    private var view: ISearchSecondaryProjectView? = null
    private var context: Context? = null

    override fun attachView(view: ISearchSecondaryProjectView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadHighlightProject() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.highlight()?.enqueue(object : Callback<ListSecondaryHouseResponse> {
            override fun onFailure(call: Call<ListSecondaryHouseResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryHouseResponse>?, response: Response<ListSecondaryHouseResponse>?) {

                val data = response?.body()?.data
                if (data != null) {
                    view?.bindingHighlightProjects(data)
                }
            }
        })
    }

    override fun searchProject(page: Int, keyword: String) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.search(page, Constant.PAGE_SIZE_SEARCH, keyword)?.enqueue(object : Callback<ListSecondaryHouseResponse> {
            override fun onFailure(call: Call<ListSecondaryHouseResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListSecondaryHouseResponse>?, response: Response<ListSecondaryHouseResponse>?) {

                val data = response?.body()?.data
                if (data != null) {
                    data.paging?.let { paging ->
                        val canLoadMore = page < paging.total
                        view?.bindingProjectsWithKeyword(data, canLoadMore)
                    }
                }
            }
        })
    }
}
