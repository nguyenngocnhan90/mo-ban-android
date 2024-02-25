package com.moban.flow.projectdeal

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.document.Document
import com.moban.model.response.BaseListResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//  Created by H. Len Vo on 6/16/2020.
//  Copyright © 2019 H. Len Vo. All rights reserved.
//

class ProjectDealsPresenterIml : BaseMvpPresenter<IProjectDealsView>, IProjectDealsPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService = retrofit?.create(ProjectService::class.java)

    private var view: IProjectDealsView? = null
    private var context: Context? = null

    override fun attachView(view: IProjectDealsView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getDeals(projectId: Int, page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.getListDeals(projectId, page)?.enqueue(object : Callback<BaseListResponse<Document>> {
            override fun onFailure(call: Call<BaseListResponse<Document>>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<BaseListResponse<Document>>?, response: Response<BaseListResponse<Document>>?) {
                response?.body()?.let {
                    it.data?.let { deals ->
                        view?.bindingDealsList(deals)
                    }
                }
            }
        })
    }
}
