package com.moban.flow.reservation

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.moban.LHApplication
import com.moban.enum.ReservationImageType
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.model.data.deal.Promotion
import com.moban.model.param.NewReservationParam
import com.moban.model.response.BaseListResponse
import com.moban.model.response.PhotoResponse
import com.moban.model.response.deal.DealResponse
import com.moban.model.response.project.ProjectResponse
import com.moban.model.response.user.ListInterestsResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.CityService
import com.moban.network.service.DealService
import com.moban.network.service.ImageService
import com.moban.network.service.ProjectService
import com.moban.util.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationPresenterIml: BaseMvpPresenter<IReservationView>, IReservationPresenter {
    private var view: IReservationView? = null
    private var context: Context? = null

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val imageService = retrofit?.create(ImageService::class.java)
    private val dealService = retrofit?.create(DealService::class.java)
    private val projectService = retrofit?.create(ProjectService::class.java)
    private val cityService: CityService? = retrofit?.create(CityService::class.java)

    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IReservationView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun upload(bitmap: Bitmap, type: ReservationImageType, retry: Int) {
        val currentContext = context!!

        if (!NetworkUtil.hasConnection(currentContext)) {
            view?.uploadImageFailed(type)
            return
        }
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmap)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)

        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData("booking-invoice", "img-" + DateUtil.currentTimeSeconds() + ".jpg", partImg)

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    upload(bitmap, type, retry + 1)
                } else {
                    view?.uploadImageFailed(type)
                }
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                if (response?.body() != null) {
                    response.body()?.let {
                        if (!it.success || it.data.isEmpty()) {
                            view?.uploadImageFailed(type)
                            return
                        }

                        val urlImg = it.data[0].photo_Link
                        view?.uploadImageCompleted(urlImg, type)
                    }
                } else {
                    view?.uploadImageFailed(type)
                }
            }
        })
    }

    override fun new(param: NewReservationParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            noNetworkDialog = DialogUtil.showNetworkError(currentContext, View.OnClickListener {
                noNetworkDialog?.dismiss()
                new(param)
            })
            return
        }

        dealService?.new(param)?.enqueue(object : Callback<DealResponse> {
            override fun onFailure(call: Call<DealResponse>?, t: Throwable?) {
                view?.newReservationFailed(null)
            }

            override fun onResponse(call: Call<DealResponse>?, response: Response<DealResponse>?) {
                response?.let { it ->
                    var message: String? = null
                    if (it.isSuccessful) {
                        it.body()?.let { dealResponse ->
                            if (dealResponse.success) {
                                dealResponse.data?.let {
                                    view?.newReservationCompleted(it)
                                    return
                                }
                            }
                            message = dealResponse.error
                        }
                    }
                    view?.newReservationFailed(message)
                }
            }
        })
    }

    override fun newSimple(param: NewReservationParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            noNetworkDialog = DialogUtil.showNetworkError(currentContext, View.OnClickListener {
                noNetworkDialog?.dismiss()
                newSimple(param)
            })
            return
        }

        dealService?.newSimple(param)?.enqueue(object : Callback<DealResponse> {
            override fun onFailure(call: Call<DealResponse>?, t: Throwable?) {
                view?.newReservationFailed(null)
            }

            override fun onResponse(call: Call<DealResponse>?, response: Response<DealResponse>?) {
                response?.let { it ->
                    var message: String? = null
                    if (it.isSuccessful) {
                        it.body()?.let { dealResponse ->
                            if (dealResponse.success) {
                                dealResponse.data?.let {
                                    view?.newReservationCompleted(it)
                                    return
                                }
                            }
                            message = dealResponse.error
                        }
                    }
                    view?.newReservationFailed(message)
                }
            }
        })
    }

    override fun update(param: NewReservationParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            noNetworkDialog = DialogUtil.showNetworkError(currentContext, View.OnClickListener {
                noNetworkDialog?.dismiss()
                update(param)
            })
            return
        }

        dealService?.update(param)?.enqueue(object : Callback<DealResponse> {
            override fun onFailure(call: Call<DealResponse>?, t: Throwable?) {
                view?.updateReservationFailed(null)
            }

            override fun onResponse(call: Call<DealResponse>?, response: Response<DealResponse>?) {
                response?.let { it ->
                    var message: String? = null
                    if (it.isSuccessful) {
                        it.body()?.let { dealResponse ->
                            if (dealResponse.success) {
                                dealResponse.data?.let {
                                    view?.updateReservationCompleted(it)
                                    return
                                }
                            }
                            message = dealResponse.error
                        }
                    }
                    view?.updateReservationFailed(message)
                }
            }
        })
    }

    override fun interests(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.interests(id)?.enqueue(object : Callback<ListInterestsResponse> {
            override fun onFailure(call: Call<ListInterestsResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListInterestsResponse>?, response: Response<ListInterestsResponse>?) {
                response?.body()?.let {it ->
                    if (it.success) {
                        it.data?.let {
                            view?.bindingInterests(it.list)
                        }
                    }
                }
            }
        })
    }

    override fun loadPromotions(id: Int) {
        projectService?.promotions(id)?.enqueue(object : Callback<BaseListResponse<Promotion>> {
            override fun onFailure(call: Call<BaseListResponse<Promotion>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<BaseListResponse<Promotion>>?, response: Response<BaseListResponse<Promotion>>?) {
                response?.body()?.let {it ->
                    if (it.success) {
                        it.data?.let {
                            view?.bindingPromotions(it.list)
                        }
                    }
                }
            }
        })
    }

    override fun loadProjectDetail(id: Int) {
        val service = retrofit?.create(ProjectService::class.java)
        service?.project(id)?.enqueue(object : Callback<ProjectResponse> {
            override fun onFailure(call: Call<ProjectResponse>?, t: Throwable?) {
                view?.bindingProjectDetailFailed()
            }

            override fun onResponse(call: Call<ProjectResponse>?, response: Response<ProjectResponse>?) {
                response?.body()?.let { it ->
                    if (it.success) {
                        it.data.let { project ->
                            project?.let {
                                view?.bindingProjectDetail(it)
                                return
                            }
                        }
                    }
                }
                view?.bindingProjectDetailFailed()
            }
        })
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
