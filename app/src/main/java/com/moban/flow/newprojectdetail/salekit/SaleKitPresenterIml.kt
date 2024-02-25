package com.moban.flow.newprojectdetail.salekit

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.document.DocumentGroupResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


/**
 * Created by H. Len Vo on 6/28/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class SaleKitPresenterIml: BaseMvpPresenter<ISaleKitView>, ISaleKitPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private var view: ISaleKitView? = null
    private var context: Context? = null

    override fun attachView(view: ISaleKitView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
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
}
