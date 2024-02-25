package com.moban.flow.reservation.update

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.moban.LHApplication
import com.moban.model.param.NewReservationParam
import com.moban.model.response.PhotoResponse
import com.moban.model.response.deal.DealResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.DealService
import com.moban.network.service.ImageService
import com.moban.util.BitmapUtil
import com.moban.util.DateUtil
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateReservationPresenterIml : BaseMvpPresenter<IUpdateReservationView>, IUpdateReservationPresenter {

    private var view: IUpdateReservationView? = null
    private var context: Context? = null

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val imageService = retrofit?.create(ImageService::class.java)
    private val dealService = retrofit?.create(DealService::class.java)

    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IUpdateReservationView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun upload(bitmap: Bitmap, retry: Int) {
        val currentContext = context!!

        if (!NetworkUtil.hasConnection(currentContext)) {
            view?.uploadImageFailed()
            return
        }
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmap)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)

        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData("chung-tu", "img-" + DateUtil.currentTimeSeconds() + ".jpg", partImg)

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    upload(bitmap, retry + 1)
                } else {
                    view?.uploadImageFailed()
                }
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                if (response?.body() != null) {
                    response.body()?.let {
                        if (!it.success || it.data.isEmpty()) {
                            view?.uploadImageFailed()
                            return
                        }

                        val urlImg = it.data[0].photo_Link
                        view?.uploadImageCompleted(urlImg)
                    }
                } else {
                    view?.uploadImageFailed()
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
}