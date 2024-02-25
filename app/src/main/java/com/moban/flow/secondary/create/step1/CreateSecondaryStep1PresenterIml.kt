package com.moban.flow.secondary.create.step1

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.CityService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CreateSecondaryStep1PresenterIml: BaseMvpPresenter<ICreateSecondaryStep1View>, ICreateSecondaryStep1Presenter {
    private var view: ICreateSecondaryStep1View? = null
    private var context: Context? = null
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val cityService: CityService? = retrofit?.create(CityService::class.java)

    override fun attachView(view: ICreateSecondaryStep1View) {
        this.view = view
        context = view.getContext()
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
                response?.let {
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
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            view?.bindingDistrict(id, it)
                        }
                    }
                }
            }
        })
    }
}
