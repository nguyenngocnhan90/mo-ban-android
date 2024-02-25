package com.moban.flow.projects

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.enum.ProjectHighlightType
import com.moban.enum.SortType
import com.moban.model.data.project.Project
import com.moban.model.response.BaseListResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by H. Len Vo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class ProjectFragmentPresenterIml : BaseMvpPresenter<IProjectsFragmentView>, IProjectsFragmentPresenter {
    private var view: IProjectsFragmentView? = null
    private lateinit var context: Context
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService = retrofit?.create(ProjectService::class.java)

    override fun attachView(view: IProjectsFragmentView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadProjects(page: Int, sortType: SortType, filter: Int?, projectType: ProjectHighlightType) {
        var noNetworkDialog: Dialog? = null
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                loadProjects(page, sortType, filter, projectType)
            })
            return
        }

        val handler = object : Callback<BaseListResponse<Project>> {
            override fun onFailure(call: Call<BaseListResponse<Project>>?, t: Throwable?) {
                view?.bindingProjectFailed()
            }

            override fun onResponse(call: Call<BaseListResponse<Project>>?, response: Response<BaseListResponse<Project>>?) {
                val data = response?.body()?.data
                if (data == null) {
                    view?.bindingProjectFailed()
                    return
                }
                val projects = data.list
                val statistics = data.statistics.toMutableList()
                val paging = data.paging
                view?.bindingProject(projects, statistics, paging)
            }
        }

        when (projectType) {
            ProjectHighlightType.NONE -> {
                projectService?.projects(page, Constant.PAGE_SIZE_SEARCH, sortType.type, filter)?.enqueue(handler)
            }
            ProjectHighlightType.F1 -> {
                projectService?.f1Highlights(page, Constant.PAGE_SIZE_SEARCH, sortType.type, filter)?.enqueue(handler)
            }
            ProjectHighlightType.F2 -> {
                projectService?.f2Highlights(page, Constant.PAGE_SIZE_SEARCH, sortType.type, filter)?.enqueue(handler)
            }
            else -> {}
        }
    }
}
