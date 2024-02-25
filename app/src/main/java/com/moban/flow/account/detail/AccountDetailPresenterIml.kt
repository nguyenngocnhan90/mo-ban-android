package com.moban.flow.account.detail

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Post
import com.moban.model.response.LoginResponse
import com.moban.model.response.PhotoResponse
import com.moban.model.response.badge.ListBadgeResponse
import com.moban.model.response.post.ListPostResponse
import com.moban.model.response.post.PostResponse
import com.moban.model.response.post.PostStatusResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ImageService
import com.moban.network.service.PostService
import com.moban.network.service.UserService
import com.moban.util.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 5/3/18.
 */
class AccountDetailPresenterIml : BaseMvpPresenter<IAccountDetailView>, IAccountDetailPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)
    private val postService = retrofit?.create(PostService::class.java)
    private val photoService = retrofit?.create(ImageService::class.java)

    private var view: IAccountDetailView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IAccountDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadBadges(userId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.myBadges(userId)?.enqueue(object : Callback<ListBadgeResponse> {
            override fun onFailure(call: Call<ListBadgeResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListBadgeResponse>?, response: Response<ListBadgeResponse>?) {
                response?.body()?.data?.list?.let {
                    view?.bindingUserBadges(it)
                }
            }
        })
    }

    override fun loadUserDetail(userId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.user(userId)?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.body()?.data?.let {
                    view?.bindingUserDetail(it)
                }
            }
        })
    }

    override fun loadFeeds(userId: Int, currentId: Int?, isRefreshing: Boolean) {
        userService?.myPosts(userId, currentId)?.enqueue(object : Callback<ListPostResponse> {
            override fun onFailure(call: Call<ListPostResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListPostResponse>?, response: Response<ListPostResponse>?) {
                response?.body()?.data?.let { data ->
                    val feeds = data.list

                    if (!feeds.isEmpty()) {
                        val lowestId = feeds.last().id
                        val canLoadMore = if (data.paging != null) {
                            data.paging!!.paging_can_load_more
                        } else false

                        view?.bindingPosts(feeds, canLoadMore, isRefreshing, lowestId)
                    }
                }
            }
        })
    }

    override fun like(post: Post) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
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
                noNetworkDialog?.dismiss()
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

    override fun follow(postId: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
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
                noNetworkDialog?.dismiss()
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

    override fun downloadPhoto(photo: Photo) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                downloadPhoto(photo)
            })
            return
        }

        if (!Permission.checkPermissionWriteExternalStorage(context)) {
            return
        }

        photoService?.download(photo.photo_Link)?.enqueue(object : Callback<ResponseBody> {
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
            return
        }

        photoService?.trackSave(photo.id)?.enqueue(object : Callback<PhotoResponse> {
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
}
