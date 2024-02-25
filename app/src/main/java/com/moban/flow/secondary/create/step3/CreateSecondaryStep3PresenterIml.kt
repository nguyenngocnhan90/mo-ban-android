package com.moban.flow.secondary.create.step3

import android.content.Context
import android.util.Log
import com.moban.LHApplication
import com.moban.model.data.BitmapUpload
import com.moban.model.response.PhotoResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ImageService
import com.moban.util.BitmapUtil
import com.moban.util.DateUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

class CreateSecondaryStep3PresenterIml: BaseMvpPresenter<ICreateSecondaryStep3View>, ICreateSecondaryStep3Presenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val imageService = retrofit?.create(ImageService::class.java)
    private var imageURLs: HashMap<Long, BitmapUpload> = HashMap()

    private var view: ICreateSecondaryStep3View? = null
    private var context: Context? = null

    override fun attachView(view: ICreateSecondaryStep3View) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun uploadImages(images: ArrayList<BitmapUpload>) {
        for (bitmap in images) {
            imageURLs[bitmap.time] = bitmap
            uploadAnImage(bitmap)
        }
    }

    override fun removeImage(image: BitmapUpload) {
        var key: Long = 0
        for ((idx, bitmapUpload) in imageURLs) {
            if (bitmapUpload.time == image.time) {
                key = idx
            }
        }
        if (key != 0L) {
            imageURLs.remove(key)
        }

    }

    private fun checkUploadCompleted(): Boolean {
        for ((_, bitmapUpload) in imageURLs) {
            if (!bitmapUpload.uploaded) {
                return false
            }
        }
        return true
    }

    private fun validateImageUrls(): Boolean {
        for ((_, bitmapUpload) in imageURLs) {
            if (bitmapUpload.url == "") {
                return false
            }
        }
        return true
    }

    private fun getImageUrls(): MutableList<String> {
        val urls : MutableList<String> = ArrayList()
        for ((_, bitmapUpload) in imageURLs) {
            urls.add(bitmapUpload.url)
        }
        return urls
    }

    private fun uploadAnImage(bitmapUpload: BitmapUpload, retry: Int = 1) {
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmapUpload.bitmap!!)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)
        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData("new-secondary-image", "img-" + DateUtil.currentTimeSeconds() + ".jpg", partImg)

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    uploadAnImage(bitmapUpload, retry + 1)
                } else {
                    processPhotoResponse(bitmapUpload.time, null)
                }
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                response?.body()?.let {
                    val urlImg = it.data[0].photo_Link
                    processPhotoResponse(bitmapUpload.time, urlImg)
                }
            }
        })
    }

    @Synchronized private fun processPhotoResponse(time: Long, imageUrl: String?) {
        Log.i("LH", "Step 3 = uploaded: $imageUrl")
        val bitmapUpload = imageURLs[time] ?: return

        bitmapUpload.uploaded = true
        imageUrl?.let {
            bitmapUpload.url = it
        }
        imageURLs[time] = bitmapUpload

        if (imageUrl == null) {
            view?.showUploadImageFailed(bitmapUpload)
        }

        if (!checkUploadCompleted()) {
            Log.i("LH", "Step 3 = continue upload next pic")
            return
        }
        Log.i("LH", "Step 3 = Done upload image")
        view?.uploadComplete()
    }
}
