package com.moban.flow.newprojectdetail.subscribe

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.project.SalePackage
import com.moban.model.param.project.JoinParam
import com.moban.model.response.ApiResponse
import com.moban.model.response.BaseListResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.DialogUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


/**
 * Created by H. Len Vo on 7/1/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class ProjectSubscribePresenterIml: BaseMvpPresenter<IProjectSubscribeView>, IProjectSubscribePresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private var view: IProjectSubscribeView? = null
    private var context: Context? = null

    override fun attachView(view: IProjectSubscribeView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadPackage(id: Int) {
        projectService?.subscribers(id)?.enqueue(object : Callback<BaseListResponse<SalePackage>> {
            override fun onFailure(call: Call<BaseListResponse<SalePackage>>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<BaseListResponse<SalePackage>>?, response: Response<BaseListResponse<SalePackage>>?) {
                val data = response?.body()?.data?.list
                if (data != null) {
                    view?.bindingPackages(data)
                }
            }
        })
    }

    override fun joinPackage(id: Int, code: String) {
        val param = JoinParam()
        param.code = code
        projectService?.join(id, param)?.enqueue(object : Callback<ApiResponse> {
            override fun onFailure(call: Call<ApiResponse>?, t: Throwable?) {
                t?.printStackTrace()
                DialogUtil.showInfoDialog(context!!, "", "Không thể đăng ký bán.", "OK", null)
            }

            override fun onResponse(call: Call<ApiResponse>?, response: Response<ApiResponse>?) {
                val success = response?.body()?.success ?: false
                if (success) {
                    val message = response?.body()?.message ?: ""
                    DialogUtil.showInfoDialog(context!!, "", message, "OK", null)
                } else {
                    val message = response?.body()?.error ?: "Không thể đăng ký bán."
                    DialogUtil.showErrorDialog(context!!, "", message, "OK", null)
                }
            }
        })
    }
}
