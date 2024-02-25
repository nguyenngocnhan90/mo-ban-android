package com.moban.flow.secondary.project

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.response.secondary.project.ListSecondaryProjectResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.SecondaryProjectService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondaryProjectPresenterIml: BaseMvpPresenter<ISecondaryProjectView>, ISecondaryProjectPresenter {
    private val retrofit = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val secondaryService = retrofit?.create(SecondaryProjectService::class.java)

    private var view: ISecondaryProjectView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: ISecondaryProjectView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadProjects(page: Int) {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                loadProjects(page)
            })
            return
        }

        secondaryService?.projects(page, Constant.PAGE_SIZE_SEARCH)?.enqueue(object : Callback<ListSecondaryProjectResponse> {
            override fun onFailure(call: Call<ListSecondaryProjectResponse>?, t: Throwable?) {
                view?.bindingLoadSecondaryFailed()
            }

            override fun onResponse(call: Call<ListSecondaryProjectResponse>?, response: Response<ListSecondaryProjectResponse>?) {
                if (response?.body() == null) {
                    view?.bindingLoadSecondaryFailed()
                    return
                }

                response.body()?.let { it ->
                    it.data?.let {
                        var canLoadMore = false
                        var total = 0
                        it.paging?.let {paging ->
                            canLoadMore = page < paging.total
                            total = paging.total_items
                        }

                        view?.bindingSecondaryProject(it, canLoadMore, total)
                    }
                }
            }

        })
    }
}
