package com.moban.flow.reservation.list

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.user.DealsAdapter
import com.moban.adapter.user.HeaderAdapter
import com.moban.constant.Constant
import com.moban.enum.*
import com.moban.flow.deals.dealdetail.DealDetailActivity
import com.moban.flow.projects.searchproject.SearchProjectActivity
import com.moban.flow.reservation.ReservationActivity
import com.moban.flow.reservation.update.UpdateReservationActivity
import com.moban.handler.DealItemHandler
import com.moban.handler.RecyclerItemListener
import com.moban.handler.ReservationMenuHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.deal.Deal
import com.moban.model.data.project.Project
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogBottomSheetUtil
import com.moban.util.DialogUtil
import com.moban.util.Permission
import kotlinx.android.synthetic.main.activity_deals.*
import kotlinx.android.synthetic.main.activity_list_reservation.*
import kotlinx.android.synthetic.main.activity_salary.*
import kotlinx.android.synthetic.main.item_project_detail_empty.view.*
import kotlinx.android.synthetic.main.nav.view.*

class ListReservationActivity : BaseMvpActivity<IListReservationView, IListReservationPresenter>(),
    IListReservationView {
    override var presenter: IListReservationPresenter = ListReservationPresenterIml()

    private var project: Project? = null
    private var user: User? = null
    private var filter: DealFilter = DealFilter.ALL

    private var statusAdapter: HeaderAdapter = HeaderAdapter()
    private val dealAdapter = DealsAdapter()

    private var combinedDealStatus: CombinedDealStatus? = null
    private var page: Int = 1

    private var isCurrentUser: Boolean = false

    private val dealStatuses: Array<CombinedDealStatus> = arrayOf(
        CombinedDealStatus.all(),
        CombinedDealStatus.waitingForApprove(),
        CombinedDealStatus.datCho(),
        CombinedDealStatus.cocChoDuyet(),
        CombinedDealStatus.coc(),
        CombinedDealStatus.hopDongChoDuyet(),
        CombinedDealStatus.hopDong()
    )

    override fun getLayoutId(): Int {
        return R.layout.activity_list_reservation
    }

    companion object {
        private const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        const val BUNDLE_KEY_USER = "BUNDLE_KEY_USER"
        const val BUNDLE_KEY_DEAL_FILTER = "BUNDLE_KEY_DEAL_FILTER"
        private const val BUNDLE_KEY_SIMPLE_DEAL = "BUNDLE_KEY_SIMPLE_DEAL"

        fun start(context: Context, user: User, filter: DealFilter = DealFilter.ALL) {
            val intent = Intent(context, ListReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USER, user)
            bundle.putSerializable(BUNDLE_KEY_DEAL_FILTER, filter)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, project: Project, isQuickDeal: Boolean = false) {
            val intent = Intent(context, ListReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)

            if (isQuickDeal) {
                bundle.putBoolean(BUNDLE_KEY_SIMPLE_DEAL, true)
            }

            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        reservation_list_nav.nav_tvTitle.text = getString(R.string.reservation_list)
        reservation_list_nav.nav_add.visibility = View.VISIBLE
        reservation_list_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        }

        if (intent.hasExtra(BUNDLE_KEY_SIMPLE_DEAL)) {
            val isSimpleDeal = bundle?.getBoolean(BUNDLE_KEY_SIMPLE_DEAL) ?: false
            if (isSimpleDeal) {
                project?.let {
                    ReservationActivity.start(this, it)
                }
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_USER)) {
            user = bundle?.getSerializable(BUNDLE_KEY_USER) as User
        } else {
            user = LocalStorage.user()
        }
        isCurrentUser = user?.isCurrentUser() ?: false
        dealAdapter.isMyDeal = isCurrentUser

        if (intent.hasExtra(BUNDLE_KEY_DEAL_FILTER)) {
            filter = bundle?.getSerializable(BUNDLE_KEY_DEAL_FILTER) as DealFilter
        }

        reservation_list_nav.nav_tvTitle.text = getString(R.string.transaction_list)

        if (project != null) {
            project?.let { project ->
                reservation_list_nav.nav_tvTitle.text =
                    getString(R.string.transaction_list) + " - " + project.product_Name
            }
        } else if (filter == DealFilter.REVIEWABLE) {
            reservation_list_nav.nav_tvTitle.text = getString(R.string.reviewable_deal)
        }

        if (project == null) {
            dealAdapter.isSameProject = false
        }

        initRecyclerStatus()
        initRecycleView()
        reloadData()

        reservation_list_refresh_products.setOnRefreshListener {
            reloadData()
        }

        reservation_list_nav.nav_add.visibility = if (project == null) View.GONE else View.VISIBLE
        reservation_list_nav.nav_add.setOnClickListener {
            project?.let {
                ReservationActivity.start(this@ListReservationActivity, it, isQuickDeal = true)
            }
        }
    }

    private fun initRecyclerStatus() {
        if (filter == DealFilter.REVIEWABLE || !isCurrentUser) {
            reservation_list_deal_statuses.visibility = View.GONE
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        reservation_list_deal_statuses.layoutManager = layoutManager
        reservation_list_deal_statuses.adapter = statusAdapter

        statusAdapter.texts = dealStatuses.map { it.name }

        statusAdapter.recyclerItemListener = object : RecyclerItemListener {
            override fun onClick(view: View, index: Int) {
                if (index < dealStatuses.count() && statusAdapter.currentIndex != index) {
                    statusAdapter.currentIndex = index
                    statusAdapter.notifyDataSetChanged()

                    dealAdapter.setDeals(ArrayList(), false)

                    combinedDealStatus = dealStatuses[index]
                    reloadData()
                }
            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        reservation_list_recycleView.layoutManager = layoutManager
        reservation_list_recycleView.adapter = dealAdapter
        reservation_list_recycleView.isNestedScrollingEnabled = false

        dealAdapter.listener = object : DealItemHandler {
            override fun onClicked(deal: Deal) {
                if (project == null) {
                    user?.let {
                        DealDetailActivity.start(this@ListReservationActivity, it, deal)
                    }
                    return
                }

                val isEdit =
                    deal.getApproveStatus() == ApproveStatus.Waiting || deal.getApproveStatus() == ApproveStatus.WatingForFixing
                if (isEdit) {
                    project?.let {
                        ReservationActivity.start(this@ListReservationActivity, it, deal, isEdit)
                    }
                } else if (deal.getApproveStatus() == ApproveStatus.Approved) {
                    user?.let {
                        DealDetailActivity.start(this@ListReservationActivity, it, deal)
                    }
                }
            }

            override fun onLongClicked(deal: Deal) {
                DialogBottomSheetUtil.showDialogReservationMenu(
                    this@ListReservationActivity,
                    deal,
                    true,
                    object : ReservationMenuHandler {
                        override fun onCopy() {
                            ReservationActivity.startCopy(
                                this@ListReservationActivity,
                                project,
                                deal
                            )
                        }

                        override fun onCancel() {
                            var dialog: Dialog? = null
                            dialog = DialogUtil.showConfirmDialog(this@ListReservationActivity,
                                true,
                                getString(R.string.deal_cancel),
                                getString(R.string.deal_cancel_confirm),
                                getString(R.string.skip),
                                getString(R.string.agree),
                                null,
                                View.OnClickListener {
                                    dialog?.dismiss()
                                    reservation_progressbar.visibility = View.VISIBLE
                                    presenter.cancel(deal.id)
                                })
                        }

                        override fun onRefundNumber() {
                            UpdateReservationActivity.start(
                                this@ListReservationActivity,
                                deal,
                                DealStatus.CANCELED
                            )
                        }

                        override fun onReSubmit() {
                            ReservationActivity.startCopy(
                                this@ListReservationActivity,
                                project,
                                deal
                            )
                        }

                        override fun onChangeToDeposit() {
                            UpdateReservationActivity.start(
                                this@ListReservationActivity,
                                deal,
                                DealStatus.DEPOSITED
                            )
                        }

                        override fun onChangeToContract() {
                            UpdateReservationActivity.start(
                                this@ListReservationActivity,
                                deal,
                                DealStatus.CONTRACTED
                            )
                        }
                    })
            }

            override fun onMainAction(deal: Deal) {
                when {
                    deal.canDeposit() -> {
                        UpdateReservationActivity.start(
                            this@ListReservationActivity,
                            deal,
                            DealStatus.DEPOSITED
                        )
                    }
                    deal.canMakeContract() -> {
                        UpdateReservationActivity.start(
                            this@ListReservationActivity,
                            deal,
                            DealStatus.CONTRACTED
                        )
                    }
                    else -> {
                        val phone = deal.admin_Phone
                        if (phone.isNotEmpty()) {
                            Permission.openCalling(this@ListReservationActivity, phone)
                        }
                    }
                }
            }

            override fun onTimeOut() {
                reloadData()
            }
        }
    }

    private fun reloadData() {
        reservation_list_refresh_products.isRefreshing = true

        project?.let { project ->
            presenter.loadProjectDeals(project.id, combinedDealStatus)
        } ?: run {
            user?.let { user ->
                presenter.loadAllDeals(user, page, combinedDealStatus, filter)
            }
        }
    }

    override fun bindingDeals(deals: List<Deal>, canLoadMore: Boolean) {
        reservation_list_refresh_products.isRefreshing = false
        reservation_list_recycleView.visibility = if (deals.isEmpty()) View.GONE else View.VISIBLE

        reservation_list_empty.visibility = if (deals.isEmpty()) View.VISIBLE else View.GONE
        reservation_list_empty.project_reservation_now.visibility = View.VISIBLE

        var emptyTitle = if (project != null) getString(R.string.reservation_list_for_project_empty) else getString(R.string.reservation_list_empty)
        if (!isCurrentUser) {
            emptyTitle = "Không có booking nào"
            reservation_list_empty.project_reservation_now.visibility = View.GONE
        }
        reservation_list_empty.project_detail_empty_text.text = emptyTitle

        reservation_list_empty.project_reservation_now.setOnClickListener {
            project?.let { project ->
                ReservationActivity.start(this@ListReservationActivity, project, isQuickDeal = true)
            } ?: run {
                SearchProjectActivity.start(this@ListReservationActivity, SearchForType.RESERVATION)
            }
        }

        dealAdapter.setDeals(deals, canLoadMore)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.RESERVATION_REQUEST) {
            data?.let {
                reloadData()
            }
        }
    }

    override fun cancelReservationCompleted() {
        reservation_progressbar.visibility = View.GONE
        reloadData()
    }

    override fun cancelReservationFailed() {
        reservation_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(
            this, getString(R.string.deal_cancel_failed_title),
            getString(R.string.deal_cancel_failed_desc), getString(R.string.ok), null
        )
    }
}
