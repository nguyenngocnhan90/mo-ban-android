package com.moban.flow.secondary.detail

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.response.secondary.SecondaryHouseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.SecondaryHouseService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondaryProjectDetailPresenterIml: BaseMvpPresenter<ISecondaryProjectDetailView>, ISecondaryProjectDetailPresenter {
    private val retrofit = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val secondaryService = retrofit?.create(SecondaryHouseService::class.java)

    private var view: ISecondaryProjectDetailView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: ISecondaryProjectDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadProject(id: Int) {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                loadProject(id)
            })
            return
        }

        secondaryService?.find(id)?.enqueue(object : Callback<SecondaryHouseResponse> {
            override fun onFailure(call: Call<SecondaryHouseResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<SecondaryHouseResponse>?, response: Response<SecondaryHouseResponse>?) {
                response?.body()?.data?.let {
                    view?.bindingProjectDetail(it)
                }
            }
        })
    }

    override fun requestContact(id: Int) {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                requestContact(id)
            })
            return
        }

        secondaryService?.requestContact(id)?.enqueue(object : Callback<SecondaryHouseResponse> {
            override fun onFailure(call: Call<SecondaryHouseResponse>?, t: Throwable?) {
                view?.bindingRequestContactFailed("")
            }

            override fun onResponse(call: Call<SecondaryHouseResponse>?, response: Response<SecondaryHouseResponse>?) {
                if (response?.body() == null) {
                    view?.bindingRequestContactFailed("")
                    return
                }
                response.body()?.let {
                    view?.bindingRequestContact(it)
                }
            }
        })
    }
}
