package com.moban.flow.newprojectdetail.cart

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.project.BlockResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


/**
 * Created by H. Len Vo on 6/30/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class ProjectCartPresenterIml: BaseMvpPresenter<IProjectCartView>, IProjectCartPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private var view: IProjectCartView? = null
    private var context: Context? = null

    override fun attachView(view: IProjectCartView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadApartments(id: Int, blockId: Int) {
        projectService?.apartments(id, blockId)?.enqueue(object : Callback<BlockResponse> {
            override fun onFailure(call: Call<BlockResponse>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<BlockResponse>?, response: Response<BlockResponse>?) {
                val data = response?.body()?.data
                if (data != null) {
                    view?.bindingApartments(data, blockId)
                }
            }
        })
    }
}
