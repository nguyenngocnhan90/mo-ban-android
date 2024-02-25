package com.moban.flow.projects.rating

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.LHApplication
import com.moban.R
import com.moban.constant.Constant
import com.moban.flow.newpost.NewPostActivity
import com.moban.flow.newprojectdetail.ProjectDetailActivity
import com.moban.model.data.project.Project
import com.moban.model.data.project.ProjectRating
import com.moban.model.param.project.ProjectRatingParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import kotlinx.android.synthetic.main.activity_new_project_rating.*
import kotlinx.android.synthetic.main.nav.view.*

class NewProjectRatingActivity :
    BaseMvpActivity<INewProjectRatingView, INewProjectRatingPresenter>(),
    INewProjectRatingView {

    override var presenter: INewProjectRatingPresenter = NewProjectRatingPresenterIml()

    private var project: Project? = null

    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(context: Activity, project: Project) {
            val intent = Intent(context, NewProjectRatingActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)
            context.startActivityForResult(intent, Constant.NEW_PROJECT_RATING_REQUEST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_new_project_rating
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(ProjectDetailActivity.BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        }

        new_project_rating_tbToolbar.title = getString(R.string.review)

        project?.let {
            new_project_rating_tv_project_name.text = it.product_Name
        }

        new_project_rating_btn_review.setOnClickListener {
            submitRating()
        }

        new_project_rating_nav.nav_imgBack.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LHApplication.instance.getFbManager()?.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.NEW_POST_REQUEST -> {
                data?.let {
                    finish()
                }
            }
        }
    }

    private fun submitRating() {
        val juridical = new_project_rating_rate_juridical.progress
        val hostTrust = new_project_rating_rate_host_trust.progress
        val location = new_project_rating_rate_location.progress
        val price = new_project_rating_rate_price.progress
        val utility = new_project_rating_rate_design_utility.progress

        if (juridical == 0 || hostTrust == 0 || location == 0 || price == 0 || utility == 0) {
            DialogUtil.showErrorDialog(
                this, getString(R.string.review_not_rating_title),
                "Vui lòng chọn đầy đủ đánh giá của bạn về dự án", getString(R.string.ok), null
            )
            return
        }

        val param = ProjectRatingParam()
        param.juridical = juridical.toDouble()
        param.host_Trust = hostTrust.toDouble()
        param.location = location.toDouble()
        param.price = price.toDouble()
        param.design_Utility = utility.toDouble()

        if (!NetworkUtil.hasConnection(this)) {
            return
        }

        new_project_rating_progress.visibility = View.VISIBLE
        project?.let {
            presenter.submitRating(it.id, param)
        }
    }

    override fun stopLoading() {
        new_project_rating_progress.visibility = View.GONE
    }

    override fun submittedRating(rating: ProjectRating) {
        val intent = Intent(this, NewProjectRatingActivity::class.java)
        setResult(Constant.NEW_PROJECT_RATING_REQUEST, intent)

        var dialog: Dialog? = null
        dialog = DialogUtil.showConfirmDialog(this,
            false,
            "",
            "Cảm ơn bạn đã đánh giá cho dự án này! Bạn có muốn nhận xét vài lời về dự án không?",
            getString(R.string.yes),
            getString(R.string.no),
            View.OnClickListener {
                dialog?.dismiss()
                NewPostActivity.start(this, rating.id, project?.id ?: 0)
                finish()
            },
            View.OnClickListener {
                dialog?.dismiss()
                finish()
            })
    }

    override fun error(error: String) {
        DialogUtil.showErrorDialog(
            this, "",
            error, getString(R.string.ok), null
        )
    }
}