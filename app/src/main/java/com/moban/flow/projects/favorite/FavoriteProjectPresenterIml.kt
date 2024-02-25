package com.moban.flow.projects.favorite

import android.content.Context
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.data.project.Project
import com.moban.model.response.project.ListProjectResponse
import com.moban.model.response.project.ProjectFavoriteResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 5/15/18.
 */
class FavoriteProjectPresenterIml : BaseMvpPresenter<IFavoriteProjectView>, IFavoriteProjectPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService: UserService? = retrofit?.create(UserService::class.java)
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)

    private var view: IFavoriteProjectView? = null
    private var context: Context? = null

    override fun attachView(view: IFavoriteProjectView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadFavoriteProjects(page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.favoriteProjects(page, Constant.PAGE_SIZE_SEARCH)?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
                t.toString()
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

    override fun unFavoriteProject(project: Project) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.unFavorite(project.id)?.enqueue(object : Callback<ProjectFavoriteResponse> {
            override fun onFailure(call: Call<ProjectFavoriteResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ProjectFavoriteResponse>?, response: Response<ProjectFavoriteResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        view?.unFavoriteProject(project)
                    }
                }

            }
        })
    }
}
