package com.moban.flow.review

import com.moban.model.data.popup.Review
import com.moban.mvp.BaseMvpView

interface IReviewView: BaseMvpView {
    fun fillDataInformation(review: Review)
    fun submitReviewCompleted()
    fun submitReviewFailed()
    fun loadReviewFailed()
}
