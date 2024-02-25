package com.moban.flow.main

import android.content.Context
import com.moban.LHApplication
import com.moban.enum.LinkmartMainCategory
import com.moban.model.data.linkmart.LinkmartBrand
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.notification.Notification
import com.moban.model.response.SimpleResponse
import com.moban.model.response.notification.ListNotification
import com.moban.model.response.notification.ListNotificationResponse
import com.moban.model.response.post.ListTopicResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.NotificationService
import com.moban.network.service.PostService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by thangnguyen on 12/18/17.
 */
class MainPresenterIml : BaseMvpPresenter<IMainView>, IMainPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()

    private val notificationService = retrofit?.create(NotificationService::class.java)
    private val postService = retrofit?.create(PostService::class.java)

    private var view: IMainView? = null
    private var context: Context? = null

    override fun attachView(view: IMainView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadNotifications() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        notificationService?.notifications(1)?.enqueue(object : Callback<ListNotificationResponse> {
            override fun onFailure(call: Call<ListNotificationResponse>?, t: Throwable?) {
                view?.bindingNotifications(ListNotification())
            }

            override fun onResponse(call: Call<ListNotificationResponse>?, response: Response<ListNotificationResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { list ->
                            view?.bindingNotifications(list)
                        }
                    }
                }
            }
        })
    }

    override fun loadLmartBrands() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.brands()?.enqueue(object : Callback<List<LinkmartBrand>> {
            override fun onFailure(call: Call<List<LinkmartBrand>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<LinkmartBrand>>?, response: Response<List<LinkmartBrand>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { list ->
                            LHApplication.instance.lhCache.brands = list
                        }
                    }
                }
            }
        })
    }

    override fun loadCategoriesLinkmart() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.categories(parent = LinkmartMainCategory.linkMart.value)?.enqueue(object : Callback<List<LinkmartCategory>> {
            override fun onFailure(call: Call<List<LinkmartCategory>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<LinkmartCategory>>?, response: Response<List<LinkmartCategory>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { categories ->
                            LHApplication.instance.lhCache.linkmartCategories = categories.sortedBy { cate -> cate.menu_order }
                        }
                    }
                }
            }
        })
    }

    override fun loadCategoriesLinkbook() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.categories(parent = LinkmartMainCategory.linkBook.value)?.enqueue(object : Callback<List<LinkmartCategory>> {
            override fun onFailure(call: Call<List<LinkmartCategory>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<LinkmartCategory>>?, response: Response<List<LinkmartCategory>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { categories ->
                            LHApplication.instance.lhCache.linkBookCategories = categories.sortedBy { cate -> cate.menu_order }
                        }
                    }
                }
            }
        })
    }

    override fun loadCategoriesLinkhub() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        LHApplication.instance.getLinkmartService()?.categories(parent = LinkmartMainCategory.linkHub.value)?.enqueue(object : Callback<List<LinkmartCategory>> {
            override fun onFailure(call: Call<List<LinkmartCategory>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<LinkmartCategory>>?, response: Response<List<LinkmartCategory>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { categories ->
                            LHApplication.instance.lhCache.linkHubCategories = categories.sortedBy { cate -> cate.menu_order }
                        }
                    }
                }
            }
        })
    }

    override fun markAsRead(notification: Notification) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        notificationService?.markAsRead(notification.id)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                response?.body()?.let {
                }
            }
        })
    }

    override fun loadPostTopics() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        postService?.topics()?.enqueue(object : Callback<ListTopicResponse> {
            override fun onFailure(call: Call<ListTopicResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ListTopicResponse>,
                response: Response<ListTopicResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        LHApplication.instance.lhCache.postTopics = it.list
                    }
                }
            }
        })
    }

    override fun loadPostFilterTopics() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        postService?.filterTopics()?.enqueue(object : Callback<ListTopicResponse> {
            override fun onFailure(call: Call<ListTopicResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ListTopicResponse>,
                response: Response<ListTopicResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        LHApplication.instance.lhCache.postFilterTopics = it.list
                    }
                }
            }
        })
    }
}
