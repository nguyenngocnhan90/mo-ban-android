package com.moban.flow.newprojectdetail

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.document.Document
import com.moban.model.data.project.Project
import com.moban.model.data.project.ProjectActivity
import com.moban.model.response.BaseListResponse
import com.moban.model.response.document.DocumentGroupResponse
import com.moban.model.response.project.ProjectResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by lenvo on 6/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class ProjectDetailPresenterIml : BaseMvpPresenter<IProjectDetailView>, IProjectDetailPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private var view: IProjectDetailView? = null
    private var context: Context? = null

    override fun attachView(view: IProjectDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadProjectDetail(id: Int) {
        val service = retrofit?.create(ProjectService::class.java)
        service?.project(id)?.enqueue(object : Callback<ProjectResponse> {
            override fun onFailure(call: Call<ProjectResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ProjectResponse>?, response: Response<ProjectResponse>?) {
                val project = response?.body()?.data
                if (project != null) {
                    view?.bindingProjectDetail(project)
                }
            }
        })
    }

    override fun loadSaleKits(id: Int) {
        projectService?.saleKits(id)?.enqueue(object : Callback<DocumentGroupResponse> {
            override fun onFailure(call: Call<DocumentGroupResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<DocumentGroupResponse>?, response: Response<DocumentGroupResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        view?.bindingSaleKitsProject(it.data)
                    }
                }

            }
        })
    }

    override fun loadRelatedProduct(id: Int) {
        projectService?.related_products(id)?.enqueue(object : Callback<BaseListResponse<Project>> {
            override fun onFailure(call: Call<BaseListResponse<Project>>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<BaseListResponse<Project>>?, response: Response<BaseListResponse<Project>>?) {
                val projects = response?.body()?.data?.list
                if (projects != null) {
                    view?.bindingRelatedProducts(projects)
                }
            }
        })
    }

    override fun loadSpecialDeal(id: Int) {
        projectService?.special_deals(id)?.enqueue(object : Callback<BaseListResponse<Document>> {
            override fun onFailure(call: Call<BaseListResponse<Document>>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<BaseListResponse<Document>>?, response: Response<BaseListResponse<Document>>?) {
                val documents = response?.body()?.data?.list
                if (documents != null) {
                    view?.bindingSpecialDeal(documents)
                }
            }
        })
    }

    override fun loadRecentActivity(id: Int) {
        projectService?.recent_activities(id)?.enqueue(object : Callback<BaseListResponse<ProjectActivity>> {
            override fun onFailure(call: Call<BaseListResponse<ProjectActivity>>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<BaseListResponse<ProjectActivity>>?, response: Response<BaseListResponse<ProjectActivity>>?) {
                val documents = response?.body()?.data?.list
                if (documents != null) {
                    view?.bindingRecentActivites(documents)
                }
            }
        })
    }
}
