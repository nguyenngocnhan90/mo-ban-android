package com.moban.flow.review

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.constant.Constant
import com.moban.model.data.popup.Review
import com.moban.model.param.review.UserReviewParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.nav.view.*

class ReviewActivity : BaseMvpActivity<IReviewView, IReviewPresenter>(), IReviewView {
    override var presenter: IReviewPresenter = ReviewPresenterIml()
    private var reviewId: Int = 0
    private var review: Review? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_review
    }

    companion object {
        private const val BUNDLE_KEY_REVIEW = "BUNDLE_KEY_REVIEW"
        private const val BUNDLE_KEY_REVIEW_ID = "BUNDLE_KEY_REVIEW_ID"
        fun start(activity: Activity, review: Review) {
            val intent = Intent(activity, ReviewActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_REVIEW, review)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.REVIEW_REQUEST)
        }

        fun start(activity: Activity, reviewId: Int) {
            val intent = Intent(activity, ReviewActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_REVIEW_ID, reviewId)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.REVIEW_REQUEST)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        review_tbToolbar.nav_tvTitle.text = getString(R.string.review_service)
        review_tbToolbar.nav_imgBack.setOnClickListener {
            finish()
        }

        if (intent.hasExtra(BUNDLE_KEY_REVIEW)) {
            val review = bundle?.getSerializable(BUNDLE_KEY_REVIEW) as Review
            reviewId = review.id
            fillDataInformation(review)
        }

        if (intent.hasExtra(BUNDLE_KEY_REVIEW_ID)) {
            reviewId = bundle?.getSerializable(BUNDLE_KEY_REVIEW_ID) as Int
            presenter.loadReview(reviewId)
        }

        review_btn_review.setOnClickListener {
            submitReview()
        }
    }

    override fun fillDataInformation(review: Review) {
        this.review = review
        review_tv_title.text = review.title
        review_tv_sub_title.text = review.sub_title
    }

    private fun submitReview() {
        val comment = review_tv_comment.text.toString()
        val rate = review_rate.progress

        if (rate == 0) {
            DialogUtil.showErrorDialog(this, getString(R.string.review_is_not_rating_title),
                    getString(R.string.review_is_not_rating_desc), getString(R.string.ok), null)
            return
        }

        val review = review ?: return
        val param = UserReviewParam(review)
        param.comment = comment
        param.rating = rate.toDouble()

        presenter.submitReview(param)

        review_progressbar.visibility = View.VISIBLE
    }

    override fun submitReviewCompleted() {
        review_progressbar.visibility = View.GONE
        DialogUtil.showInfoDialog(this, getString(R.string.review_success_title),
                getString(R.string.review_success_desc), getString(R.string.ok), null)
    }

    override fun submitReviewFailed() {
        review_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this, getString(R.string.review_fail_title),
                getString(R.string.review_fail_desc), getString(R.string.ok), null)
    }

    override fun loadReviewFailed() {
        DialogUtil.showErrorDialog(this, getString(R.string.load_review_fail_title),
                getString(R.string.load_review_fail_desc), getString(R.string.ok), null)
    }
}
