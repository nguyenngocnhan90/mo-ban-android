package com.moban.flow.newprojectdetail.news

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.post.ListPostResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by lenvo on 6/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class ProjectNewPresenterIml : BaseMvpPresenter<IProjectNewsView>, IProjectNewPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private var view: IProjectNewsView? = null
    private var context: Context? = null

    override fun attachView(view: IProjectNewsView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadPost(id: Int, lowestId: Int?) {
        projectService?.posts(id, lowestId)?.enqueue(object : Callback<ListPostResponse> {
            override fun onFailure(call: Call<ListPostResponse>?, t: Throwable?) {
                t?.printStackTrace()
                view?.finishLoading()
            }

            override fun onResponse(call: Call<ListPostResponse>?, response: Response<ListPostResponse>?) {
                view?.finishLoading()

                response?.body()?.data?.let { data ->
                    val feeds = data.list

                    if (feeds.isNotEmpty()) {
                        val canLoadMore = if (data.paging != null) {
                            data.paging!!.paging_can_load_more
                        } else false
                        view?.bindingPosts(feeds, feeds.last().id, canLoadMore)
                    }
                }
            }
        })
    }

    override fun loadReviewPost(id: Int, lowestId: Int?) {
        projectService?.reviewed_posts(id, lowestId)?.enqueue(object : Callback<ListPostResponse> {
            override fun onFailure(call: Call<ListPostResponse>?, t: Throwable?) {
                t?.printStackTrace()
                view?.finishLoading()
            }

            override fun onResponse(call: Call<ListPostResponse>?, response: Response<ListPostResponse>?) {
                view?.finishLoading()

                response?.body()?.data?.let { data ->
                    val feeds = data.list

                    if (feeds.isNotEmpty()) {
                        val canLoadMore = if (data.paging != null) {
                            data.paging!!.paging_can_load_more
                        } else false
                        view?.bindingPosts(feeds, feeds.last().id, canLoadMore)
                    }
                }
            }
        })
    }
}
