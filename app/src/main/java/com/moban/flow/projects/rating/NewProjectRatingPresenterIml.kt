package com.moban.flow.projects.rating

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.project.ProjectRating
import com.moban.model.param.project.ProjectRatingParam
import com.moban.model.response.BaseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NewProjectRatingPresenterIml : BaseMvpPresenter<INewProjectRatingView>,
    INewProjectRatingPresenter {

    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private var view: INewProjectRatingView? = null
    private var context: Context? = null

    override fun attachView(view: INewProjectRatingView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun submitRating(projectId: Int, param: ProjectRatingParam) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.addRating(projectId, param)
            ?.enqueue(object : Callback<BaseResponse<ProjectRating>> {
                override fun onFailure(call: Call<BaseResponse<ProjectRating>>, t: Throwable) {
                    view?.stopLoading()
                }

                override fun onResponse(
                    call: Call<BaseResponse<ProjectRating>>,
                    response: Response<BaseResponse<ProjectRating>>
                ) {
                    view?.stopLoading()

                    val body = response.body()
                    body?.data?.let {
                        view?.submittedRating(it)
                    } ?: run {
                        view?.error(body?.error ?: "")
                    }
                }
            })
    }
}