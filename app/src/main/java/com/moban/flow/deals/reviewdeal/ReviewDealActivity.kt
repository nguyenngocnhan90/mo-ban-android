package com.moban.flow.deals.reviewdeal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.helper.LocalStorage
import com.moban.model.data.deal.Deal
import com.moban.model.data.deal.ReviewDeal
import com.moban.model.param.user.NewDealReviewParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.activity_review_deal.*
import kotlinx.android.synthetic.main.nav.view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class ReviewDealActivity : BaseMvpActivity<IReviewDealView, IReviewDeallPresenter>(), IReviewDealView {
    override var presenter: IReviewDeallPresenter = ReviewDealPresenterIml()
    private lateinit var deal: Deal
    companion object {
        const val BUNDLE_KEY_DEAL = "BUNDLE_KEY_DEAL"
        const val BUNDLE_KEY_DEAL_ID = "BUNDLE_KEY_DEAL_ID"
        const val BUNDLE_KEY_DEAL_REVIEW = "BUNDLE_KEY_DEAL_REVIEW"

        fun start(activity: Activity, deal: Deal) {
            val intent = Intent(activity, ReviewDealActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.REVIEW_DEAL_REQUEST)
        }

        fun start(activity: Activity, dealId: Int) {
            val intent = Intent(activity, ReviewDealActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_DEAL_ID, dealId)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.REVIEW_DEAL_REQUEST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_review_deal
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras

        var dealId = 0
        if (intent.hasExtra(BUNDLE_KEY_DEAL)) {
            deal = bundle?.getSerializable(BUNDLE_KEY_DEAL) as Deal
            fillDataInformation(deal)
            dealId = deal.id
        } else if (intent.hasExtra(BUNDLE_KEY_DEAL_ID)) {
            dealId = bundle?.getSerializable(BUNDLE_KEY_DEAL_ID) as Int
            presenter.loadDeal(dealId)
        }

        review_deal_nav.nav_tvTitle.text = getText(R.string.deal_review)
        review_deal_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        setGAScreenName("Review Deal  $dealId", GACategory.ACCOUNT)

        deal_review_btn_review.setOnClickListener {
            submitReview()
        }
        initKeyBoardListener()
    }

    private fun initKeyBoardListener() {
        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            review_deal_keyboard_area.visibility = if (isOpen) View.VISIBLE else View.GONE
            if (isOpen) {
                Handler().postDelayed({
                    review_deal_scroll_view.scrollTo(0, review_deal_keyboard_area.height)
                }, 100)
            }
        }
    }

    override fun fillDataInformation(deal: Deal) {
        this.deal = deal
        deal_review_btn_review.visibility = View.VISIBLE
        review_deal_view_rate_qldt.visibility = if(deal.can_Review_QLDT) View.VISIBLE else View.GONE
        review_deal_view_header_qldt.visibility = if(deal.can_Review_QLDT) View.VISIBLE else View.GONE

        LHPicasso.loadImage(deal.admin_Avatar, review_deal_img_admin)
        LHPicasso.loadImage(deal.teamLeader_Avatar, review_deal_img_qldt)

        review_deal_tv_deal_name.text = deal.product_Name
        val labelApartmentInfo = "Mã Căn: " + deal.flat_ID + " - Khách: " + deal.customer_Name_Exact
        review_deal_tv_deal_desc.text = labelApartmentInfo

        review_deal_tv_admin_name.text = deal.admin_Name
        review_deal_tv_qldt_name.text = deal.teamLeader_Name
        if (deal.has_Review) {
            DialogUtil.showErrorDialog(this, getString(R.string.you_has_sent_review_title),
                    getString(R.string.you_has_sent_review_desc), getString(R.string.ok), View.OnClickListener {
                        finish()
            })
            deal_review_btn_review.visibility = View.GONE
        }
    }

    private fun submitReview() {
        val comment = review_deal_tv_note.text.toString()
        val adminRate = review_deal_rate_admin.progress
        val qldtRate = review_deal_rate_qldt.progress

        if (!deal.can_Review_QLDT ) {
            if (adminRate == 0) {
                DialogUtil.showErrorDialog(this, getString(R.string.review_not_rating_admin_title),
                        getString(R.string.review_not_rating_admin_desc), getString(R.string.ok), null)
                return
            }
        } else if (adminRate == 0 || qldtRate == 0) {
            DialogUtil.showErrorDialog(this, getString(R.string.review_not_rating_title),
                    getString(R.string.review_not_rating_desc), getString(R.string.ok), null)
            return
        }
        deal_review_progressbar.visibility = View.VISIBLE

        val param = NewDealReviewParam()
        param.deal_id = deal.id
        param.comment = comment
        param.rating_detail_admin = adminRate.toDouble()
        param.rating_detail_qldt = qldtRate.toDouble()
        presenter.reviewDeal(param)
    }

    override fun reviewCompleted(reviewDeal: ReviewDeal) {
        deal_review_progressbar.visibility = View.GONE
        val intent = Intent(this, ReviewDealActivity::class.java)
        intent.putExtra(BUNDLE_KEY_DEAL, deal)
        intent.putExtra(BUNDLE_KEY_DEAL_REVIEW, reviewDeal)
        setResult(Constant.REVIEW_DEAL_REQUEST, intent)

        LocalStorage.user()?.let {
            it.unreview_Deal_Count = it.unreview_Deal_Count - 1
            LocalStorage.saveUser(it)
        }

        finish()
    }

    override fun reviewFailed() {
        deal_review_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this, getString(R.string.can_not_review_title),
                getString(R.string.can_not_review_desc), getString(R.string.ok), null)
    }
}
