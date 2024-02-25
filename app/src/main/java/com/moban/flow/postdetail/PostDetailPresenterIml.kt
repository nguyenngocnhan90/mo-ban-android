package com.moban.flow.postdetail

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.data.BitmapUpload
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.param.post.CreateCommentParam
import com.moban.model.response.PhotoResponse
import com.moban.model.response.post.ListCommentResponse
import com.moban.model.response.post.NewPostResponse
import com.moban.model.response.post.PostResponse
import com.moban.model.response.post.PostStatusResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ImageService
import com.moban.network.service.PostService
import com.moban.util.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by neal on 3/13/18.
 */
class PostDetailPresenterIml : BaseMvpPresenter<IPostDetailView>, IPostDetailPresenter {

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val postService = retrofit?.create(PostService::class.java)
    private val imageService = retrofit?.create(ImageService::class.java)

    private var view: IPostDetailView? = null
    private var context: Context? = null

    private var topCommentId: Int = 0
    private var minimumCommentId: Int = 0
    private var newCommentParam: CreateCommentParam? = null

    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IPostDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun like(post: Post) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                like(post)
            })
            return
        }

        postService?.like(post.id)?.enqueue(object : Callback<PostStatusResponse> {
            override fun onFailure(call: Call<PostStatusResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostStatusResponse>?, response: Response<PostStatusResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        post.is_Liked = true

                        it.data?.let {
                            post.likes = it.likes
                            post.comments = it.comments
                        }

                        view?.reloadPost(post)
                    }
                }
            }
        })
    }

    override fun unlike(post: Post) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                unlike(post)
            })
            return
        }

        postService?.unlike(post.id)?.enqueue(object : Callback<PostStatusResponse> {
            override fun onFailure(call: Call<PostStatusResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostStatusResponse>?, response: Response<PostStatusResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        post.is_Liked = false

                        it.data?.let {
                            post.likes = it.likes
                            post.comments = it.comments
                        }

                        view?.reloadPost(post)
                    }
                }
            }
        })
    }

    override fun getPost(postId: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                getPost(postId)
            })
            return
        }

        postService?.get(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                response?.body()?.data?.let {
                    view?.reloadPost(it)
                    view?.didGetPost(it)
                }
            }
        })
    }

    override fun refreshComments(postId: Int) {
        topCommentId = 0
        getComments(postId)
    }

    override fun getComments(postId: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                getComments(postId)
            })
            return
        }

        val lowestId = if (topCommentId == 0) null else topCommentId

        postService?.comments(postId, lowestId)?.enqueue(object : Callback<ListCommentResponse> {
            override fun onFailure(call: Call<ListCommentResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListCommentResponse>?, response: Response<ListCommentResponse>?) {
                response?.body()?.data?.let { list ->
                    minimumCommentId = list.min_id

                    val comments = list.list.sortedWith(compareBy {
                        it.created_Date
                    })

                    if (topCommentId == 0) {
                        view?.setComments(comments)
                    } else {
                        view?.appendComments(comments)
                    }

                    if (comments.isNotEmpty()) {
                        comments.first().let {
                            topCommentId = it.id
                        }
                    }
                }
            }
        })
    }

    override fun postComment(
        post: Post,
        replyingComment: Comment?,
        content: String,
        photo: BitmapUpload?
    ) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                postComment(post, replyingComment, content, photo)
            })
            return
        }

        val param = CreateCommentParam()
        param.post_Id = post.id
        param.post_Comment_Content = content
        param.parent_Id = replyingComment?.id ?: 0

        photo?.let {
            newCommentParam = param
            uploadAnImage(it, 1)
        } ?: run {
            callPostComment(param)
        }
    }

    private fun callPostComment(param: CreateCommentParam, photoUrl: String? = null) {
        photoUrl?.let {
            param.photo_Url = it
        }

        postService?.comment(param.post_Id, param)?.enqueue(object : Callback<NewPostResponse> {
            override fun onFailure(call: Call<NewPostResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<NewPostResponse>?, response: Response<NewPostResponse>?) {
                response?.body()?.data?.let {
                    view?.addMyComment(it)
                }
            }
        })
    }

    override fun follow(postId: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                follow(postId)
            })
            return
        }

        postService?.follow(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                response?.body()?.data?.let {
                    view?.reloadPost(it)
                    view?.showFollowedMessage()
                }
            }
        })
    }

    override fun unfollow(postId: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                unfollow(postId)
            })
            return
        }

        postService?.unfollow(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                response?.body()?.data?.let {
                    view?.reloadPost(it)
                }
            }
        })
    }

    /**
     * Network dialog
     */

    private fun dismissNetworkDialog() {
        noNetworkDialog?.let {
            it.dismiss()
        }
    }

    override fun downloadPhoto(photo: Photo) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                dismissNetworkDialog()
                downloadPhoto(photo)
            })
            return
        }

        if (!Permission.checkPermissionWriteExternalStorage(context)) {
            return
        }

        imageService?.download(photo.photo_Link)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.let {
                    val fileName = FileUtil.getFileNameFromURL(photo.photo_Link)
                    if (BitmapUtil.saveImage(context, it.byteStream(), fileName)) {
                        sendTracking(photo, Constant.NUM_RETRY_FAILING)
                    }
                }
            }
        })
    }

    private fun sendTracking(photo: Photo, numOfRetry: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        if (numOfRetry == 0) {
            return;
        }

        imageService?.trackSave(photo.id)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                if (response?.body() == null) {
                    sendTracking(photo, numOfRetry - 1)
                }

                response?.body()?.let {
                    if (!it.success) {
                        sendTracking(photo, numOfRetry - 1)
                    }
                }
            }
        })
    }

    override fun trackShare(postId: Int, numOfRetry: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }
        if (numOfRetry == 0) {
            return;
        }

        postService?.trackShare(postId)?.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<PostResponse>?, response: Response<PostResponse>?) {
                if (response?.body() == null) {
                    trackShare(postId, numOfRetry - 1)
                }

                response?.body()?.let {
                    if (!it.success) {
                        trackShare(postId, numOfRetry - 1)
                    }
                }
            }
        })
    }

    private fun uploadAnImage(bitmapUpload: BitmapUpload, retry: Int = 1) {
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmapUpload.bitmap!!)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)
        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData("new-post-image", "img-" + DateUtil.currentTimeSeconds() + ".jpg", partImg)

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    uploadAnImage(bitmapUpload, retry + 1)
                } else {
                    processPhotoResponse(null)
                }
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                response?.body()?.let {
                    val urlImg = it.data[0].photo_Link
                    processPhotoResponse(urlImg)
                }
            }
        })
    }

    @Synchronized private fun processPhotoResponse(imageUrl: String?) {
        newCommentParam?.let {
            callPostComment(it, imageUrl)
        }
    }
}
