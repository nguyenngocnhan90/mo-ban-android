package com.moban.flow.review

import com.moban.model.param.review.UserReviewParam
import com.moban.mvp.BaseMvpPresenter

interface IReviewPresenter : BaseMvpPresenter<IReviewView> {
    fun submitReview(param: UserReviewParam)
    fun loadReview(reviewId: Int)
}
