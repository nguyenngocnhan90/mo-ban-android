package com.moban.flow.home

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import com.moban.LHApplication
import com.moban.model.data.Photo
import com.moban.model.data.homedeal.HomeDeal
import com.moban.model.data.post.Post
import com.moban.model.data.user.User
import com.moban.model.response.BaseListResponse
import com.moban.model.response.project.ListProjectResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.*
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

/**
 * Created by lenvo on 4/26/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class NewHomeFragmentPresenterIml : BaseMvpPresenter<INewHomeFragmentView>, INewHomeFragmentPresenter {
    private var view: INewHomeFragmentView? = null
    private lateinit var context: Context
    private var noNetworkDialog: Dialog? = null
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val postService = retrofit?.create(PostService::class.java)
    private val projectService = retrofit?.create(ProjectService::class.java)
    private val levelService = retrofit?.create(LevelService::class.java)
    private val userService = retrofit?.create(UserService::class.java)
    private val homeService = retrofit?.create(HomeService::class.java)

    override fun attachView(view: INewHomeFragmentView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun posts(lowestId: Int?) {
        val context = this.context!!
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                posts(lowestId)
            })
            return
        }

        Log.i("LEN", "Load posts with lowest ID: $lowestId")
        postService?.homes(lowestId)?.enqueue(object: Callback<BaseListResponse<Post>> {
            override fun onFailure(call: Call<BaseListResponse<Post>>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseListResponse<Post>>, response: Response<BaseListResponse<Post>>) {
                response.body()?.data?.let { data ->
                    val feeds = data.list

                    if (feeds.isNotEmpty()) {
                        Log.i("LEN", "Loaded posts with lowest ID: $lowestId => New lowest Id: ${feeds.last().id}")
                        val canLoadMore = if (data.paging != null) {
                            data.paging!!.paging_can_load_more
                        } else false
                        view?.bindingPosts(feeds, feeds.last().id, canLoadMore)
                    }
                }
            }
        })
    }

    override fun banners() {
        postService?.banners()?.enqueue(object: Callback<BaseListResponse<Photo>> {
            override fun onFailure(call: Call<BaseListResponse<Photo>>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseListResponse<Photo>>, response: Response<BaseListResponse<Photo>>) {
                response.body()?.data?.let { data ->
                    val list = data.list
                    view?.bindingBanner(list)
                }
            }
        })
    }

    override fun topPartners() {
        levelService?.topPartners()?.enqueue(object: Callback<BaseListResponse<User>> {
            override fun onFailure(call: Call<BaseListResponse<User>>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseListResponse<User>>, response: Response<BaseListResponse<User>>) {
                response.body()?.data?.let { data ->
                    val list = data.list
                    view?.bindingTopPartner(list)
                }
            }
        })
    }

    override fun getHomeDeals() {
        homeService?.deals()?.enqueue(object: Callback<BaseListResponse<HomeDeal>> {
            override fun onFailure(call: Call<BaseListResponse<HomeDeal>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<BaseListResponse<HomeDeal>>, response: Response<BaseListResponse<HomeDeal>>) {
                val homeDeals = response.body()?.data?.list
                if (homeDeals != null) {
                    view?.bindingHomeDeals(homeDeals)
                }
            }

        })
    }

    override fun getHighlightProjects() {
        projectService?.highlight()?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {
                val data = response?.body()?.data
                if (data != null) {
                    view?.bindingHighlightProjects(data.list)
                    view?.bindingStatistics(data.statistics)
                }
            }
        })
    }

    override fun getRecentViewedProjects() {
        projectService?.viewed()?.enqueue(object : Callback<ListProjectResponse> {
            override fun onFailure(call: Call<ListProjectResponse>?, t: Throwable?) {
                t.toString()
                view?.bindingRecentViewedProjects(ArrayList())
            }

            override fun onResponse(call: Call<ListProjectResponse>?, response: Response<ListProjectResponse>?) {
                val list = response?.body()?.data?.list ?: ArrayList()
                view?.bindingRecentViewedProjects(list)
            }
        })
    }
}
