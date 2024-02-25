package com.moban.flow.review

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.popup.Review
import com.moban.model.param.review.UserReviewParam
import com.moban.model.response.review.ReviewResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserReviewService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewPresenterIml: BaseMvpPresenter<IReviewView>, IReviewPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val reviewService = retrofit?.create(UserReviewService::class.java)

    private var view: IReviewView? = null
    private var context: Context? = null

    override fun attachView(view: IReviewView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun submitReview(param: UserReviewParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            view?.submitReviewFailed()
            return
        }

        reviewService?.submitReview(param)?.enqueue(object : Callback<ReviewResponse> {
            override fun onFailure(call: Call<ReviewResponse>?, t: Throwable?) {
                view?.submitReviewFailed()
            }

            override fun onResponse(call: Call<ReviewResponse>?, response: Response<ReviewResponse>?) {
                if (response?.body() != null) {
                    response.body()?.let {
                        view?.submitReviewCompleted()
                    }
                } else {
                    view?.submitReviewFailed()
                }
            }
        })
    }

    override fun loadReview(reviewId: Int) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            view?.submitReviewFailed()
            return
        }

        reviewService?.get(reviewId)?.enqueue(object : Callback<Review> {
            override fun onFailure(call: Call<Review>?, t: Throwable?) {
                view?.submitReviewFailed()
            }

            override fun onResponse(call: Call<Review>?, response: Response<Review>?) {
                if (response?.body() != null) {
                    response.body()?.let {
                        view?.fillDataInformation(it)
                    }
                }
            }
        })
    }
}
