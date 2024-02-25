package com.moban.flow.projects.searchproject

import android.content.Context
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.response.project.ListProjectResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 7/22/18.
 */
class SearchProjectPresenterIml : BaseMvpPresenter<ISearchProjectView>, ISearchProjectPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)

    private var view: ISearchProjectView? = null
    private var context: Context? = null

    override fun attachView(view: ISearchProjectView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadProject(page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.highlight()?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {

                val data = response?.body()?.data
                if (data != null) {
                    data.paging?.let { paging ->
                        val canLoadMore = page < paging.total
                        view?.bindingProjects(data, canLoadMore)
                    }
                }
            }
        })
    }

    override fun searchProject(page: Int, keyword: String) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.search(page, Constant.PAGE_SIZE_SEARCH, keyword)?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {

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
