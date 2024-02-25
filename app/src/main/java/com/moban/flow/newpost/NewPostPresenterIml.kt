package com.moban.flow.newpost

import android.content.Context
import android.util.Log
import com.moban.LHApplication
import com.moban.model.data.BitmapUpload
import com.moban.model.param.post.CreatePostParam
import com.moban.model.response.PhotoResponse
import com.moban.model.response.post.PostResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ImageService
import com.moban.network.service.PostService
import com.moban.util.BitmapUtil
import com.moban.util.DateUtil
import com.moban.util.NetworkUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by LenVo on 4/17/18.
 */
class NewPostPresenterIml : BaseMvpPresenter<INewPostView>, INewPostPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val postService = retrofit?.create(PostService::class.java)
    private val imageService = retrofit?.create(ImageService::class.java)

    private var view: INewPostView? = null
    private var context: Context? = null

    private var content: String = ""
    private var youtubeId: String = ""
    private var topicId: Int = 0
    private var imageURLs: HashMap<Long, BitmapUpload> = HashMap()
    private var requestSubmit: Boolean = false
    private var ratingId: Int = 0
    private var projectId: Int = 0

    override fun attachView(view: INewPostView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun createPost(
        content: String,
        youtubeId: String,
        topicId: Int,
        images: ArrayList<BitmapUpload>,
        ratingId: Int,
        projectId: Int
    ) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        this.content = content
        this.youtubeId = youtubeId
        this.topicId = topicId
        this.ratingId = ratingId
        this.projectId = projectId
        requestSubmit = true

        if (images.isEmpty()) {
            submitPost(content, ArrayList())
            return
        }

        if (checkUploadCompleted()) {
            if (!validateImageUrls()) {
                view?.submitPostFailed()
                return
            }
            submitPost(content, ArrayList(getImageUrls()))
        }
    }

    private fun submitPost(
        content: String, imageURLs: ArrayList<String>
    ) {
        val param = CreatePostParam()
        param.post_Content = content
        param.post_Photos = imageURLs
        param.topic_Id = topicId
        param.youtube_Id = youtubeId
        param.product_Id = projectId
        param.rating_Id = ratingId

        if (projectId > 0) {
            param.post_Status = 13
        }

        postService?.post(param)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {
                view?.submitPostFailed()
            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                response?.body()?.let {
                    it.data?.let { post ->
                        view?.submitPostCompleted(post)
                    }
                }
            }
        })
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
        val urls: MutableList<String> = ArrayList()
        for ((_, bitmapUpload) in imageURLs) {
            urls.add(bitmapUpload.url)
        }
        return urls
    }

    private fun uploadAnImage(bitmapUpload: BitmapUpload, retry: Int = 1) {
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmapUpload.bitmap!!)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)
        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData(
            "new-secondary-image",
            "img-" + DateUtil.currentTimeSeconds() + ".jpg",
            partImg
        )

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    uploadAnImage(bitmapUpload, retry + 1)
                } else {
                    processPhotoResponse(bitmapUpload.time, null)
                }
            }

            override fun onResponse(
                call: Call<PhotoResponse>?,
                response: Response<PhotoResponse>?
            ) {
                response?.body()?.let {
                    val urlImg = it.data[0].photo_Link
                    processPhotoResponse(bitmapUpload.time, urlImg)
                }
            }
        })
    }

    @Synchronized
    private fun processPhotoResponse(time: Long, imageUrl: String?) {
        Log.i("LH", "uploaded: $imageUrl")
        val bitmapUpload = imageURLs[time]

        if (bitmapUpload != null) {
            bitmapUpload.uploaded = true
            imageUrl?.let {
                bitmapUpload.url = it
            }
            imageURLs[time] = bitmapUpload
        }

        if (!checkUploadCompleted()) {
            Log.i("LH", "continue upload next pic")
            return
        }
        Log.i("LH", "Done upload image")

        if (requestSubmit) {
            if (!validateImageUrls()) {
                view?.submitPostFailed()
                return
            }
            Log.i("LH", "Done. Receive request submit => Submit new Post")
            submitPost(content, ArrayList(getImageUrls()))
        }
    }
}
