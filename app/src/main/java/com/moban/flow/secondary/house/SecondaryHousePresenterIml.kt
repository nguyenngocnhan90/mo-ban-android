package com.moban.flow.secondary.house

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.response.secondary.ListSecondaryHouseResponse
import com.moban.model.response.secondary.constant.ListSecondaryAttributeResponse
import com.moban.model.response.secondary.constant.ListSecondaryBasicConstantResponse
import com.moban.model.response.secondary.constant.ListSecondaryHouseTypeResponse
import com.moban.model.response.secondary.constant.ListSecondaryProjectBaseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.SecondaryConstantService
import com.moban.network.service.SecondaryProjectService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondaryHousePresenterIml: BaseMvpPresenter<ISecondaryHouseView>, ISecondaryHousePresenter {
    private val retrofit = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val secondaryProjectService = retrofit?.create(SecondaryProjectService::class.java)
    private val secondaryConstantService = retrofit?.create(SecondaryConstantService::class.java)

    private var view: ISecondaryHouseView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: ISecondaryHouseView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadSecondaryProject(id: Int, page: Int) {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                loadSecondaryProject(id, page)
            })
            return
        }

        secondaryProjectService?.houses(id)?.enqueue(object : Callback<ListSecondaryHouseResponse> {
            override fun onFailure(call: Call<ListSecondaryHouseResponse>?, t: Throwable?) {
                view?.bindingLoadSecondaryFailed()
            }

            override fun onResponse(call: Call<ListSecondaryHouseResponse>?, response: Response<ListSecondaryHouseResponse>?) {
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

    override fun loadBasicContants() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.attributeType()?.enqueue(object : Callback<ListSecondaryAttributeResponse> {
            override fun onFailure(call: Call<ListSecondaryAttributeResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryAttributeResponse>?, response: Response<ListSecondaryAttributeResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.secondaryAttributes = it.list
                }
            }
        })
    }

    override fun getBaseProjects() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.products()?.enqueue(object : Callback<ListSecondaryProjectBaseResponse> {
            override fun onFailure(call: Call<ListSecondaryProjectBaseResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryProjectBaseResponse>?, response: Response<ListSecondaryProjectBaseResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.secondaryBaseProjects = it.list
                    LHApplication.instance.lhCache.isLoadedSecondaryConstant = true
                }
            }
        })
    }

    override fun getHouseTypes() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.housetypes()?.enqueue(object : Callback<ListSecondaryHouseTypeResponse> {
            override fun onFailure(call: Call<ListSecondaryHouseTypeResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryHouseTypeResponse>?, response: Response<ListSecondaryHouseTypeResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.houseTypes = it.list
                }
            }
        })
    }

    override fun getTargetHouseTypes() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.targettypeshouse()?.enqueue(object : Callback<ListSecondaryBasicConstantResponse> {
            override fun onFailure(call: Call<ListSecondaryBasicConstantResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryBasicConstantResponse>?, response: Response<ListSecondaryBasicConstantResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.targetHouseTypes = it.list
                }
            }
        })
    }

    override fun getPriceUnits() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.unitprice()?.enqueue(object : Callback<ListSecondaryBasicConstantResponse> {
            override fun onFailure(call: Call<ListSecondaryBasicConstantResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryBasicConstantResponse>?, response: Response<ListSecondaryBasicConstantResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.priceUnits = it.list
                }
            }
        })
    }

    override fun getAgentPriceUnits() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.unitagent()?.enqueue(object : Callback<ListSecondaryBasicConstantResponse> {
            override fun onFailure(call: Call<ListSecondaryBasicConstantResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryBasicConstantResponse>?, response: Response<ListSecondaryBasicConstantResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.agentPriceUnits = it.list
                }
            }
        })
    }

    override fun getDirections() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.directions()?.enqueue(object : Callback<ListSecondaryBasicConstantResponse> {
            override fun onFailure(call: Call<ListSecondaryBasicConstantResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryBasicConstantResponse>?, response: Response<ListSecondaryBasicConstantResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.directions = it.list
                }
            }
        })
    }
}
