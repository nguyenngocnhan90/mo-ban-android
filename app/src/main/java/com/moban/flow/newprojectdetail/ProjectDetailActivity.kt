package com.moban.flow.newprojectdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.policy.PolicyDocumentAdapter
import com.moban.adapter.project.ProjectActivityAdapter
import com.moban.adapter.project.ProjectAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.extension.format
import com.moban.flow.newprojectdetail.cart.ProjectCartActivity
import com.moban.flow.newprojectdetail.news.ProjectNewActivity
import com.moban.flow.newprojectdetail.salekit.SaleKitActivity
import com.moban.flow.newprojectdetail.subscribe.ProjectSubscribeActivity
import com.moban.flow.notification.NotificationActivity
import com.moban.flow.pdf.PdfActivity
import com.moban.flow.postdetail.PostDetailActivity
import com.moban.flow.projectdeal.ProjectDealsActivity
import com.moban.flow.projects.rating.NewProjectRatingActivity
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.flow.reservation.list.ListReservationActivity
import com.moban.flow.signin.SignInActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.handler.DocumentBottomMenuItemHandler
import com.moban.handler.ProjectItemHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.document.Document
import com.moban.model.data.document.DocumentGroup
import com.moban.model.data.project.Project
import com.moban.model.data.project.ProjectActivity
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_project_detail.*
import kotlinx.android.synthetic.main.nav.view.*

class ProjectDetailActivity : BaseMvpActivity<IProjectDetailView, IProjectDetailPresenter>(),
    IProjectDetailView {
    override var presenter: IProjectDetailPresenter = ProjectDetailPresenterIml()
    private var project: Project? = null
    private var projectId: Int? = null
    private val relatedProjectAdapter: ProjectAdapter =
        ProjectAdapter(ProjectAdapter.PROJECT_DETAIL_HORIZONTAL)
    private val documentAdapter: PolicyDocumentAdapter = PolicyDocumentAdapter()
    private val activityAdapter = ProjectActivityAdapter()

    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        private const val BUNDLE_KEY_PROJECT_ID = "BUNDLE_KEY_PROJECT_ID"
        private const val BUNDLE_KEY_POLICY_FIRST = "BUNDLE_KEY_POLICY_FIRST"

        fun start(context: Context, project: Project) {
            val intent = Intent(context, ProjectDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, projectId: Int, showPolicyFirst: Boolean = false) {
            val intent = Intent(context, ProjectDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT_ID, projectId)
            bundle.putSerializable(BUNDLE_KEY_POLICY_FIRST, showPolicyFirst)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun startForResult(activity: Activity, project: Project) {
            val intent = Intent(activity, ProjectDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.PROJECT_DETAIL_REQUEST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_project_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        prj_detail_nav.nav_notification.visibility = View.VISIBLE
        ViewUtil.updateUnReadNotificationView(prj_detail_nav.nav_unread_notification)
        prj_detail_tbToolbar.nav_tvSearchProject.visibility = View.VISIBLE
        prj_detail_tbToolbar.nav_tvTitle.visibility = View.INVISIBLE
        prj_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        if (project == null) {
            prj_detail_scrollView.visibility = View.INVISIBLE
        }

        initRecycleView()

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
            project?.let { project ->
                projectId = project.id

                bindingProjectDetail(project)
            }
        } else if (intent.hasExtra(BUNDLE_KEY_PROJECT_ID)) {
            projectId = bundle?.getSerializable(BUNDLE_KEY_PROJECT_ID) as Int
        }

        refreshData()
        setGAScreenName("Project Detail $projectId", GACategory.PROJECT)

        prj_detail_btn_booking.setOnClickListener {
            val isBookingUnavailable = project?.isBookingUnavailable() ?: false
            if (isBookingUnavailable) {
                callAdmin()
            } else {
                val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
                if (isAnonymous) {
                    val activity = this
                    SignInActivity.start(activity, activity)
                } else {
                    val isReservable = project?.with_Dat_Cho ?: false
                    if (isReservable) {
                        viewMyReservations()
                    } else {
                        viewCart()
                    }
                }
            }
        }

        prj_detail_tbToolbar.nav_tvSearchProject.setOnClickListener {
            SearchProjectActivity.start(this)
        }

        prj_detail_tbToolbar.nav_notification.visibility = View.VISIBLE
        prj_detail_tbToolbar.nav_notification.setOnClickListener {
            NotificationActivity.start(this)
        }

        new_project_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        new_project_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        val param = prj_detail_img.layoutParams
        param.height = Device.getScreenWidth() * 9 / 16
        prj_detail_img.layoutParams = param

        prj_detail_btn_rating_now.setOnClickListener {
            project?.let {
                NewProjectRatingActivity.start(this@ProjectDetailActivity, it)
            }
        }

        prj_detail_btn_my_rating.setOnClickListener {
            project?.my_Rating?.let {
                val postId = it.post_Id
                if (postId > 0) {
                    PostDetailActivity.start(this@ProjectDetailActivity, postId, true)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LHApplication.instance.getFbManager()?.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.NEW_PROJECT_RATING_REQUEST -> {
                data?.let {
                    projectId?.let { id ->
                        presenter.loadProjectDetail(id)
                    }
                }
            }
        }
    }

    private fun refreshData() {
        projectId?.let { projectId ->
            presenter.loadProjectDetail(projectId)
            presenter.loadSaleKits(projectId)
            presenter.loadRelatedProduct(projectId)
            presenter.loadSpecialDeal(projectId)
            presenter.loadRecentActivity(projectId)
        }
    }

    private fun viewCart() {
        project?.let {
            ProjectCartActivity.start(this, it)
        }
    }

    private fun viewMyReservations() {
        project?.let {
            ListReservationActivity.start(this, it)
        }
    }

    private fun callAdmin() {
        if (!project?.user_Phone.isNullOrEmpty()) {
            Permission.openCalling(this@ProjectDetailActivity, project!!.user_Phone)
        }
    }

    private fun initRecycleView() {
        //Related products
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        prj_detail_relate_product_recycle_view.layoutManager = layoutManager
        prj_detail_relate_product_recycle_view.adapter = relatedProjectAdapter
        relatedProjectAdapter.setProjects(LHApplication.instance.lhCache.highlightProject, false)

        relatedProjectAdapter.listener = object : ProjectItemHandler {
            override fun onClicked(project: Project) {
                start(this@ProjectDetailActivity, project)
            }

            override fun onFavorite(project: Project) {
            }
        }

        //Special deal
        val specialDealLayoutManager =
            GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false)
        prj_detail_special_deal_recycle_view.layoutManager = specialDealLayoutManager
        prj_detail_special_deal_recycle_view.adapter = documentAdapter
        documentAdapter.isSpecialDeal = true
        documentAdapter.isProjectPolicy = true

        //Recent activity
        val linearLayoutManager = LinearLayoutManager(this)
        prj_detail_recent_activity_recycle_view.layoutManager = linearLayoutManager
        prj_detail_recent_activity_recycle_view.adapter = activityAdapter
    }

    private fun viewMap(location: String) {
        val url = "https://www.google.com/maps/?q=${location}"
        WebViewActivity.start(this, title = getString(R.string.map), link = url)
    }

    private fun viewZoning(zoning: String) {
        WebViewActivity.start(this, title = getString(R.string.zoning), link = zoning)
    }

    private fun openZaloProfile(link: String) {
        try {
            val uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.zing.zalo")
            startActivity(intent)
        } catch (e: Exception) {
            WebViewActivity.start(this, getString(R.string.project_website), link)
        }
    }

    private fun bindProjectRating() {
        val rating = project?.rating
        val myRating = project?.my_Rating
        val detail = rating?.detail

        val count = rating?.count ?: 0
        prj_detail_tv_review_average.text = (rating?.average ?: 0.0).format(1)
        prj_detail_tv_review_count.text = if (count > 0) "${count} đánh giá" else "Chưa có đánh giá"

        val postId = myRating?.post_Id ?: 0
        val hasMyRating = myRating != null
        val hasMyRatingPost = postId > 0
        prj_detail_btn_my_rating.visibility = if (hasMyRatingPost) View.VISIBLE else View.GONE
        prj_detail_btn_rating_now.alpha = if (!hasMyRating) 1.0f else 0.6f
        prj_detail_btn_rating_now.isEnabled = !hasMyRating
        prj_detail_btn_rating_now.setText(if (hasMyRating) R.string.reviewed else R.string.review_now)

        prj_detail_view_rating_progress_juridical_full.viewTreeObserver.addOnGlobalLayoutListener {
            val fullWidth =
                (prj_detail_view_rating_progress_juridical_full.width - 4).toDouble()

            val juridical = detail?.juridical ?: 0.0
            prj_detail_tv_rating_juridical.text = juridical.format(1)
            var param = prj_detail_view_rating_progress_juridical.layoutParams
            param.width = (juridical * fullWidth / 5.0).toInt()
            prj_detail_view_rating_progress_juridical.layoutParams = param

            val host_Trust = detail?.host_Trust ?: 0.0
            prj_detail_tv_rating_host_trust.text = host_Trust.format(1)
            param = prj_detail_view_rating_progress_host_trust.layoutParams
            param.width = (host_Trust * fullWidth / 5.0).toInt()
            prj_detail_view_rating_progress_host_trust.layoutParams = param

            val location = detail?.location ?: 0.0
            prj_detail_tv_rating_location.text = location.format(1)
            param = prj_detail_view_rating_progress_location.layoutParams
            param.width = (location * fullWidth / 5.0).toInt()
            prj_detail_view_rating_progress_location.layoutParams = param

            val price = detail?.price ?: 0.0
            prj_detail_tv_rating_price.text = price.format(1)
            param = prj_detail_view_rating_progress_price.layoutParams
            param.width = (price * fullWidth / 5.0).toInt()
            prj_detail_view_rating_progress_price.layoutParams = param

            val design_Utility = detail?.design_Utility ?: 0.0
            prj_detail_tv_rating_design_utility.text = design_Utility.format(1)
            param = prj_detail_view_rating_progress_design_utility.layoutParams
            param.width = (design_Utility * fullWidth / 5.0).toInt()
            prj_detail_view_rating_progress_design_utility.layoutParams = param
        }
    }

    override fun bindingProjectDetail(project: Project) {
        prj_detail_scrollView.visibility = View.VISIBLE
        new_project_refresh_layout.isRefreshing = false

        this.project = project
        bindProjectRating()

        prj_detail_tv_prj_name.text = project.product_Name
        val address = project.district + ", " + project.city
        prj_detail_tv_address.text = address

        LHPicasso.loadImage(project.product_Image, prj_detail_img)
        LHPicasso.loadImage(
            project.product_Icon,
            prj_detail_img_logo,
            ImageView.ScaleType.CENTER_INSIDE
        )

        val discountFee = project.finalDiscountServiceFee()
        val fee = project.finalServiceFee()
        project_tv_service_fee.text = fee
        project_tv_discount_service_fee.text = discountFee
        project_tv_service_fee_bottom.text = fee
        project_tv_discount_service_fee_bottom.text = discountFee

        project_tv_service_fee_separator.visibility =
            if (fee.isEmpty()) View.INVISIBLE else View.VISIBLE
        project_tv_service_fee.paintFlags =
            project_tv_service_fee.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        project_tv_service_fee_separator_bottom.visibility =
            if (fee.isEmpty()) View.INVISIBLE else View.VISIBLE
        project_tv_service_fee_bottom.paintFlags =
            project_tv_service_fee_bottom.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        prj_detail_tv_fee.text = project.product_Agent_Rate_Text
        prj_detail_tv_hh.text = project.finalDiscountServiceFee()
        prj_detail_tv_deposit.text = project.advance_Rate

        val price = if (project.product_Price == 0) "--" else (project.priceString() + " tr")
        prj_detail_tv_price.text = price

        val isBookingUnavailable = project.isBookingUnavailable()
        val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false

        // Check cart
        val isBookable = project.with_Cart && project.blocks.isNotEmpty()
        prj_detail_view_cart.visibility = if (isBookable) View.VISIBLE else View.GONE
        prj_detail_view_marketplace.visibility =
            if (project.market_Place_Url.isNotEmpty()) View.VISIBLE else View.GONE

        val deviceWidth = Device.getScreenWidth()
        val itemWidth = if (isBookable) (deviceWidth / 5 + BitmapUtil.convertDpToPx(
            this,
            10
        )) else deviceWidth / 4
        arrayOf(
            prj_detail_view_book,
            prj_detail_view_cart,
            prj_detail_view_marketplace,
            prj_detail_view_news,
            prj_detail_view_salekit,
            prj_detail_view_review
        ).forEach { view ->
            val layoutParams = view.layoutParams
            layoutParams.width = itemWidth
            view.layoutParams = layoutParams
        }

        prj_detail_view_salekit.setOnClickListener {
            SaleKitActivity.start(this, project)
        }
        prj_detail_img_salekit.setOnClickListener {
            SaleKitActivity.start(this, project)
        }

        prj_detail_view_news.setOnClickListener {
            ProjectNewActivity.start(this, project, 0)
        }
        prj_detail_img_news.setOnClickListener {
            ProjectNewActivity.start(this, project, 0)
        }

        prj_detail_view_review.setOnClickListener {
            ProjectNewActivity.start(this, project, 1)
        }
        prj_detail_img_review.setOnClickListener {
            ProjectNewActivity.start(this, project, 1)
        }

        prj_detail_rating_btn_all.setOnClickListener {
            ProjectNewActivity.start(this, project, 1)
        }

        prj_detail_view_cart.setOnClickListener {
            viewCart()
        }
        prj_detail_img_cart.setOnClickListener {
            viewCart()
        }

        prj_detail_view_marketplace.setOnClickListener {
            val url = project.market_Place_Url
            if (url.isNotEmpty()) {
                WebViewActivity.start(this, "Marketplace", url, oneTimeToken = true)
            }
        }

        prj_detail_view_book.setOnClickListener {
            ProjectSubscribeActivity.start(this, project)
        }
        prj_detail_img_book.setOnClickListener {
            ProjectSubscribeActivity.start(this, project)
        }

        project_pr_tv_info_intro.text = project.product_Description
        project_pr_tv_info_name.text = project.product_Name
        prj_detail_tv_info_hostname.text = project.product_Host_Name_String()

        val location = project.product_Location
        project_info_map.visibility = if (location.isNotEmpty()) View.VISIBLE else View.GONE
        project_info_map_line.visibility = if (location.isNotEmpty()) View.VISIBLE else View.GONE
        project_info_map.setOnClickListener {
            viewMap(location)
        }

        val zoning = project.quyhoach_Url
        project_info_zoning.visibility = if (zoning.isNotEmpty()) View.VISIBLE else View.GONE
        project_info_zoning_line.visibility = if (zoning.isNotEmpty()) View.VISIBLE else View.GONE
        project_info_zoning.setOnClickListener {
            viewZoning(zoning)
        }

        val website = project.product_Website
        project_info_website_tv_website.text = website
        project_info_website.visibility = if (website.isNotEmpty()) View.VISIBLE else View.GONE
        project_info_website_line.visibility = if (website.isNotEmpty()) View.VISIBLE else View.GONE

        val isZaloLink = website.toLowerCase().contains("zalo")
        project_info_website_tv_title.text = getString(if (isZaloLink) R.string.project_zalo else R.string.project_website)
        project_info_website.setOnClickListener {
            if (isZaloLink) {
                openZaloProfile(website)
            } else {
                WebViewActivity.start(this@ProjectDetailActivity, getString(R.string.project_website), website)
            }
        }

        LHPicasso.loadImage(project.user_Avatar, prj_detail_img_avatar)
        prj_detail_tv_admin_name.text = project.user_Name
        prj_detail_btn_call_admin.setOnClickListener {
            callAdmin()
        }

        val numRemainCarts = project.remain_Carts
        val bookingTitle = if (numRemainCarts > 0) "CÒN $numRemainCarts SẢN PHẨM" else ""
        prj_detail_tv_booking_title.text = bookingTitle
        prj_detail_tv_booking_title.visibility =
            if (bookingTitle.isEmpty()) View.GONE else View.VISIBLE

        if (isBookingUnavailable) {
            prj_detail_btn_booking.text = getString(R.string.contact)
        } else if (isAnonymous) {
            prj_detail_btn_booking.text = getString(R.string.project_detail_login_to_book)
        }

        prj_detail_special_deal_btn_all.setOnClickListener {
            ProjectDealsActivity.start(this@ProjectDetailActivity, project)
        }
    }

    override fun bindingSaleKitsProject(documents: List<DocumentGroup>) {
        val document = documents.firstOrNull { it.items.isNotEmpty() }?.items?.first() ?: return
        prj_detail_view_document.visibility = View.VISIBLE
        val context = this
        val dateUpdate = getString(R.string.date_update) + " " + document.getDocumentUpdatedDate()
        prj_detail_tv_document_update_date.text = dateUpdate
        prj_detail_tv_document_name.text = document.doc_Name

        prj_detail_btn_document_more.setOnClickListener {
            DialogBottomSheetUtil.showDialogDocumentMenu(context,
                PdfUtil.shouldShowDownloadBtn(document.link),
                object : DocumentBottomMenuItemHandler {
                    override fun onDownload() {
                        if (Permission.checkPermissionWriteExternalStorage(context) &&
                            Permission.checkPermissionReadExternalStorage(context)
                        ) {
                            PdfUtil.downloadFile(document.link, context)
                        }
                    }

                    override fun onShare() {
                        PdfUtil.openShareLink(context, document.link)
                    }
                })
        }

        prj_detail_view_document_item.setOnClickListener {
            val filePath = LHApplication.commonStorage.getString(document.link)
            if (filePath.isNullOrEmpty()) {
                WebViewActivity.start(
                    context,
                    document.doc_Name,
                    PdfUtil.generatePdfDocURL(document.link)
                )
            } else {
                PdfActivity.start(context, document.doc_Name, filePath)
            }
        }

        prj_detail_view_document_item.setOnLongClickListener {
            PdfUtil.openShareLink(context, document.link)
            true
        }

        prj_detail_btn_view_all_doc.setOnClickListener {
            project?.let {
                SaleKitActivity.start(this, it)
            }

        }
    }

    override fun bindingRelatedProducts(projects: List<Project>) {
        relatedProjectAdapter.setProjects(projects, false)
    }

    override fun bindingSpecialDeal(documents: List<Document>) {
        documentAdapter.setDocumentList(documents)
        prj_detail_view_special_deals.visibility =
            if (documents.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun bindingRecentActivites(activities: List<ProjectActivity>) {
        activityAdapter.updateData(activities)
    }
}
