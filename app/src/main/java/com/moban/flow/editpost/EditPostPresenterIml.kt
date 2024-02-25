package com.moban.flow.editpost

import android.content.Context
import android.util.Log
import com.moban.LHApplication
import com.moban.model.data.BitmapUpload
import com.moban.model.param.post.EditPostParam
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
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

/**
 * Created by H. Len Vo on 8/25/18.
 */
class EditPostPresenterIml: BaseMvpPresenter<IEditPostView>, IEditPostPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()

    private var view: IEditPostView? = null
    private var context: Context? = null

    private val postService = retrofit?.create(PostService::class.java)
    private val imageService = retrofit?.create(ImageService::class.java)

    private var youtubeId: String = ""
    private var topicId: Int = 0
    private var content: String = ""
    private var imageURLs: HashMap<Long, BitmapUpload> = HashMap()
    private var requestSubmit: Boolean = false

    override fun attachView(view: IEditPostView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun editPost(
        postId: Int,
        content: String,
        youtubeId: String,
        topicId: Int,
        images: ArrayList<BitmapUpload>
    ) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        this.content = content
        this.youtubeId = youtubeId
        this.topicId = topicId
        images.map {
            imageURLs[DateUtil.currentTimeSeconds()] = it
        }
        requestSubmit = true

        if (images.isEmpty()) {
            submitPost(postId, content, ArrayList())
            return
        }

        if (checkUploadCompleted()) {
            if (!validateImageUrls()) {
                view?.submitPostFailed()
                return
            }
            submitPost(postId, content, ArrayList(getImageUrls()))
        }
    }

    private fun submitPost(postId: Int, content: String, imageURLs: ArrayList<String>) {
        val param = EditPostParam()
        param.id = postId
        param.post_Content = content
        param.post_Photos = imageURLs
        param.topic_Id = topicId
        param.youtube_Id = youtubeId

        postService?.update(postId, param)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {
                view?.submitPostFailed()
                requestSubmit = false
            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                requestSubmit = false
                
                response?.body()?.let { it ->
                    it.data?.let {
                        view?.submitPostCompleted(it)
                    }
                } ?: run {
                    view?.submitPostFailed()
                }
            }
        })
    }

    override fun uploadImages(postId: Int, images: ArrayList<BitmapUpload>) {
        for (bitmap in images) {
            imageURLs[bitmap.time] = bitmap
            uploadAnImage(postId, bitmap)
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

    private fun uploadAnImage(postId: Int, bitmapUpload: BitmapUpload, retry: Int = 1) {
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmapUpload.bitmap!!)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)
        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData("newfeed-image", "img-" + DateUtil.currentTimeSeconds() + ".jpg", partImg)

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    uploadAnImage(postId, bitmapUpload, retry + 1)
                } else {
                    processPhotoResponse(postId, bitmapUpload.time, null)
                }
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                response?.body()?.let {
                    val urlImg = it.data[0].photo_Link
                    processPhotoResponse(postId, bitmapUpload.time, urlImg)
                }
            }
        })
    }

    @Synchronized private fun processPhotoResponse(postId: Int, time: Long, imageUrl: String?) {
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
            submitPost(postId, content, ArrayList(getImageUrls()))
        }
    }
}
