package com.moban.flow.secondary.searchadvance

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.model.response.ListStringResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.CityService
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 7/22/18.
 */
class SearchSecondaryProjectAdvancePresenterIml: BaseMvpPresenter<ISearchSecondaryProjectAdvanceView>, ISearchSecondaryProjectAdvancePresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val retrofitNet: Retrofit? = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)
    private val cityService: CityService? = retrofitNet?.create(CityService::class.java)

    private var view: ISearchSecondaryProjectAdvanceView? = null
    private var context: Context? = null

    override fun attachView(view: ISearchSecondaryProjectAdvanceView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadCities() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        cityService?.cities()?.enqueue(object : Callback<List<City>> {
            override fun onFailure(call: Call<List<City>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<City>>?, response: Response<List<City>>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingCities(it)
                        }
                    }
                }
            }
        })
    }

    override fun loadDistrict(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        cityService?.districts(id)?.enqueue(object : Callback<List<District>> {
            override fun onFailure(call: Call<List<District>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<District>>?, response: Response<List<District>>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingDistrict(id, it)
                        }
                    }
                }
            }
        })
    }

    override fun loadHosts() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.hosts()?.enqueue(object : Callback<ListStringResponse> {
            override fun onFailure(call: Call<ListStringResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListStringResponse>?, response: Response<ListStringResponse>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                view?.bindingHosts(it.data)
                            }
                        }
                    }
                }
            }
        })
    }
}
