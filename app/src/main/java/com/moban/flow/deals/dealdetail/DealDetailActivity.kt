package com.moban.flow.deals.dealdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.adapter.deal.DealHistoryAdapter
import com.moban.constant.Constant
import com.moban.enum.ApproveStatus
import com.moban.enum.GACategory
import com.moban.flow.deals.reviewdeal.ReviewDealActivity
import com.moban.flow.reservation.ReservationActivity
import com.moban.handler.DealBottomMenuEditHandler
import com.moban.helper.LocalStorage
import com.moban.model.data.deal.Deal
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_deal_detail.*
import kotlinx.android.synthetic.main.activity_list_reservation.*
import kotlinx.android.synthetic.main.activity_reservation.*
import kotlinx.android.synthetic.main.fragment_deal_history.*
import kotlinx.android.synthetic.main.fragment_deal_info.*
import kotlinx.android.synthetic.main.item_deal_history.view.*
import kotlinx.android.synthetic.main.nav.view.*

class DealDetailActivity : BaseMvpActivity<IDealDetailView, IDealDetailPresenter>(), IDealDetailView {

    override var presenter: IDealDetailPresenter = DealDetailPresenterIml()

    private var user: User? = null
    private var deal: Deal? = null
    private var dealId: Int? = null
    private var isCurrentUser: Boolean = true

    private var historyAdapter = DealHistoryAdapter()

    companion object {
        const val BUNDLE_KEY_USER = "BUNDLE_KEY_USER"
        const val BUNDLE_KEY_DEAL = "BUNDLE_KEY_DEAL"
        const val BUNDLE_KEY_DEAL_ID = "BUNDLE_KEY_DEAL_ID"

        fun start(activity: Activity, user: User, deal: Deal) {
            val intent = Intent(activity, DealDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USER, user)
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.DEAL_DETAIL_REQUEST)
        }

        fun start(context: Context, dealId: Int) {
            val intent = Intent(context, DealDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_DEAL_ID, dealId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_deal_detail
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras

        user = if (intent.hasExtra(BUNDLE_KEY_USER)) {
            bundle?.getSerializable(BUNDLE_KEY_USER) as User
        } else {
            LocalStorage.user()
        }
        isCurrentUser = user?.isCurrentUser() ?: true

        if (intent.hasExtra(BUNDLE_KEY_DEAL)) {
            deal = bundle?.getSerializable(BUNDLE_KEY_DEAL) as Deal

            deal?.let {
                setGAScreenName("Deal Detail ${it.id}", GACategory.ACCOUNT)
                presenter.loadDeal(it.id)
                bindingBasicInfo(it)
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_DEAL_ID)) {
            dealId = bundle?.getSerializable(BUNDLE_KEY_DEAL_ID) as Int
            setGAScreenName("Deal Detail $dealId", GACategory.ACCOUNT)
            dealId?.let {
                presenter.loadDeal(it)
            }
        }

        initAttachmentSize()
        initHistoryRecyclerView()

        val param = deal_detail_header_section.layoutParams
        param.height = Device.getScreenWidth() * 9 / 16
        deal_detail_header_section.layoutParams = param

        deal_detail_nav.nav_tvTitle.text = getText(R.string.deal_detail)
        deal_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        showPageByIndex(0)

        arrayOf(deal_tab_info, deal_tab_history).forEachIndexed { index, button ->
            button.setOnClickListener {
                showPageByIndex(index)
            }
        }
    }

    private fun initHistoryRecyclerView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        deal_history_recycler_view.layoutManager = layoutManager
        deal_history_recycler_view.adapter = historyAdapter
    }

    private fun initAttachmentSize() {
        val arrayImages = arrayOf(deal_info_img_cmnd, deal_info_img_back_cmnd,
                deal_info_payment_01, deal_info_payment_02, deal_info_payment_03, deal_info_payment_04, deal_info_payment_05,
                deal_info_deposit_01, deal_info_deposit_02, deal_info_deposit_03, deal_info_deposit_04, deal_info_deposit_05,
                deal_info_contract_01, deal_info_contract_02, deal_info_contract_03, deal_info_contract_04, deal_info_contract_05,
                deal_info_img_trang_bia, deal_info_img_trang_chu_ho, deal_info_img_trang_khach_hang)

        arrayImages.forEachIndexed { index, view ->
            val param = view.layoutParams
            val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(this, 50)) / 3
            param.width = heightView
            param.height = heightView
            view.layoutParams = param
        }
    }

    private fun bindingBasicInfo(deal: Deal) {
        deal_tv_project_name.text = deal.product_Name
        item_deal_tv_project_name.text = deal.product_Name

        if (deal.product_Image.isNotEmpty()) {
            LHPicasso.loadImage(deal.product_Image, item_deal_img_media)
        }

        if (!isCurrentUser) {
            deal_tv_customer_name.visibility = View.GONE
            deal_view_secure.visibility = View.VISIBLE
            deal_view_private_info.visibility = View.GONE
        }

        deal_tv_customer_name.text = deal.customer_Name_Exact
        deal_tv_customer_birthday.text = deal.customer_Birthday
        deal_tv_customer_cmnd.text = deal.customer_CMND
        deal_tv_customer_cmnd_issue_date.text = deal.customer_CMND_Date
        deal_tv_customer_cmnd_issue_place.text = deal.customer_CMND_Place
        deal_tv_customer_permanent_address.text = deal.homeAddress()
        deal_tv_customer_address.text = deal.address()
        deal_tv_customer_phone.text = deal.customer_Phone
        deal_tv_customer_email.text = deal.customer_Email

        deal_tv_state.text = deal.deal_Status_String
        deal_tv_state.setBackgroundColor(Color.parseColor(deal.deal_Status_Color))

        historyAdapter.deal = deal
        historyAdapter.histories = deal.full_Deal_Histories
        historyAdapter.notifyDataSetChanged()

        if (deal.flat_ID == null) {
            item_deal_flat.visibility = View.INVISIBLE
        } else {
            item_deal_flat.text = deal.flat_ID
            deal.getDealStatus()?.let {
                item_deal_flat.background = it.getBackgroundRound5(this)
            }
        }

        bindAttachments(deal)
    }

    private fun bindAttachments(deal: Deal) {
        if (!isCurrentUser) {
            arrayOf(deal_info_view_payment, deal_info_view_cmnd, deal_info_view_hokhau, deal_info_view_deposit, deal_info_view_contract).forEach {
                it.visibility = View.GONE
            }
            return
        }

        val invoices = deal.bank_Invoice_Documents() + deal.cash_Invoice_Documents()
        arrayOf(deal_info_payment_01, deal_info_payment_02, deal_info_payment_03, deal_info_payment_04, deal_info_payment_05).forEachIndexed { index, imageView ->
            if (index < invoices.size) {
                imageView.visibility = View.VISIBLE
                LHPicasso.loadImage(invoices[index].link, imageView)
            }
            else {
                imageView.visibility = View.INVISIBLE
            }
        }
        deal_info_view_payment.visibility = if (invoices.isEmpty()) View.GONE else View.VISIBLE

        val cmnds = deal.cmnd_Documents()
        arrayOf(deal_info_img_cmnd, deal_info_img_back_cmnd).forEachIndexed { index, imageView ->
            if (index < cmnds.size) {
                imageView.visibility = View.VISIBLE
                LHPicasso.loadImage(cmnds[index].link, imageView)
            }
            else {
                imageView.visibility = View.INVISIBLE
            }
        }
        deal_info_view_cmnd.visibility = if (cmnds.isEmpty()) View.GONE else View.VISIBLE

        val hoKhau_Documents = deal.hoKhau_Documents()
        arrayOf(deal_info_img_trang_bia, deal_info_img_trang_chu_ho, deal_info_img_trang_khach_hang).forEachIndexed { index, imageView ->
            if (index < hoKhau_Documents.size) {
                imageView.visibility = View.VISIBLE
                LHPicasso.loadImage(hoKhau_Documents[index].link, imageView)
            }
            else {
                imageView.visibility = View.INVISIBLE
            }
        }
        deal_info_view_hokhau.visibility = if (hoKhau_Documents.isEmpty()) View.GONE else View.VISIBLE

        val depositDocuments = deal.deposit_Documents()
        arrayOf(deal_info_deposit_01, deal_info_deposit_02, deal_info_deposit_03, deal_info_deposit_04, deal_info_deposit_05).forEachIndexed { index, imageView ->
            if (index < depositDocuments.size) {
                imageView.visibility = View.VISIBLE
                LHPicasso.loadImage(depositDocuments[index].link, imageView)
            }
            else {
                imageView.visibility = View.INVISIBLE
            }
        }
        deal_info_view_deposit.visibility = if (depositDocuments.isEmpty()) View.GONE else View.VISIBLE

        val contractDocuments = deal.contract_Documents()
        arrayOf(deal_info_contract_01, deal_info_contract_02, deal_info_contract_03, deal_info_contract_04, deal_info_contract_05).forEachIndexed { index, imageView ->
            if (index < contractDocuments.size) {
                imageView.visibility = View.VISIBLE
                LHPicasso.loadImage(contractDocuments[index].link, imageView)
            }
            else {
                imageView.visibility = View.INVISIBLE
            }
        }
        deal_info_view_contract.visibility = if (contractDocuments.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showPageByIndex(selectIndex: Int) {
        arrayOf(fragment_deal_detail_info, fragment_deal_detail_history).forEachIndexed { index, view ->
            view.visibility = if (index == selectIndex) View.VISIBLE else View.GONE
        }

        val fontNormal = Font.regularTypeface(this)
        val fontBold = Font.boldTypeface(this)
        arrayOf(deal_tab_info, deal_tab_history).forEachIndexed { index, view ->
            val isCurrentIndex = index == selectIndex

            view.typeface = if (isCurrentIndex) fontBold else fontNormal
            view.setTextColor(Util.getColor(this,
                    if (isCurrentIndex) R.color.colorPrimaryDark
                    else R.color.colorPrimaryGray))
        }
    }

    override fun bindingDeal(deal: Deal) {
        bindingBasicInfo(deal)

        this.deal = deal
        setResultData(deal)
        var currentUser = false
        LocalStorage.user()?.let {
            if (user != null) {
                currentUser = user!!.id == it.id
            }
        }

        if (deal.canEdit() && currentUser) {
            deal_detail_nav.nav_edit.visibility = View.VISIBLE
            deal_detail_nav.nav_edit.setOnClickListener {
                DialogBottomSheetUtil.showDialogMenuEditDeal(this@DealDetailActivity, object : DealBottomMenuEditHandler {
                    override fun onEdit() {
                        ReservationActivity.start(this@DealDetailActivity, deal, true)
                    }
                })
            }
        }

        fragment_deal_detail_btn_review.visibility = if (deal.can_Review && currentUser) View.VISIBLE else View.GONE
        fragment_deal_detail_btn_review.setOnClickListener {
            ReviewDealActivity.start(this, deal)
        }

        deal_view_note.visibility = if (deal.hasComment()) View.VISIBLE else View.GONE
        var statusMessage = ""

        if (deal.hasComment() && currentUser) {
            if (deal.canEdit()) {
                statusMessage = "Ghi chú của Admin: ${deal.deal_Comment} \n\n " + getString(R.string.update_now).toUpperCase()
                deal_detail_tv_reason_reject.setOnClickListener {
                    ReservationActivity.start(this, deal, true)
                }
            } else if (deal.getApproveStatus() == ApproveStatus.Denied) {
                statusMessage = getString(R.string.deal_rejected) + " ${getString(R.string.by)} ${deal.deal_Comment}"
            }
        }
        deal_detail_tv_reason_reject.text = statusMessage
        deal_detail_tv_reason_reject.visibility = if (statusMessage.isNotEmpty()) View.VISIBLE else View.GONE

        if (deal.hasComment()) {
            deal_tv_note.text = deal.deal_Comment
        }

        deal_tv_flat_it.text = deal.flat_ID
        deal_tv_product_price.text = NumberUtil.formatPrice(deal.flat_Price.toDouble(), this)
        deal_tv_booked.text = NumberUtil.formatPrice(deal.booking_Price.toDouble(), this)

        deal_tv_seller_revenue.text = NumberUtil.formatPrice(deal.deal_Agent_Revenue.toDouble(), this)
        deal_tv_seller_profit.text = NumberUtil.formatPrice(deal.deal_Agent_Commission.toDouble(), this)

        deal_tv_seller.text = deal.agent_Name
        deal_tv_leader.text = deal.teamLeader_Name
        deal_tv_director.text = deal.manager_Name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REVIEW_DEAL_REQUEST) {
            data?.let { it ->
                deal?.let {
                    it.has_Review = true
                    fragment_deal_detail_btn_review.visibility = View.GONE
                    setResultData(it)
                }
            }
        }
    }

    private fun setResultData(deal: Deal) {
        val intent = Intent(this, ReviewDealActivity::class.java)
        intent.putExtra(BUNDLE_KEY_DEAL, deal)
        setResult(Constant.DEAL_DETAIL_REQUEST, intent)
    }
}
