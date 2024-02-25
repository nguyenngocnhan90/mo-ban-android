package com.moban.flow.reservation

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.mvc.imagepicker.ImagePicker
import com.moban.R
import com.moban.adapter.address.CityAdapter
import com.moban.adapter.address.DistrictAdapter
import com.moban.constant.Constant
import com.moban.enum.ApproveStatus
import com.moban.enum.DocumentType
import com.moban.enum.GACategory
import com.moban.enum.ReservationImageType
import com.moban.extension.getLink
import com.moban.extension.isValidEmail
import com.moban.extension.isValidPhoneNumber
import com.moban.extension.toFullPriceString
import com.moban.flow.deals.reviewdeal.ReviewDealActivity
import com.moban.handler.*
import com.moban.model.data.BitmapUpload
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.model.data.deal.Deal
import com.moban.model.data.deal.Promotion
import com.moban.model.data.document.Document
import com.moban.model.data.project.Project
import com.moban.model.data.user.Interest
import com.moban.model.data.user.InterestGroup
import com.moban.model.param.DocumentParam
import com.moban.model.param.InterestParam
import com.moban.model.param.NewReservationParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_reservation.*
import kotlinx.android.synthetic.main.dialog_address.view.*
import kotlinx.android.synthetic.main.nav.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ReservationActivity : BaseMvpActivity<IReservationView, IReservationPresenter>(),
    IReservationView, DatePickerDialog.OnDateSetListener {
    override var presenter: IReservationPresenter = ReservationPresenterIml()

    private var isQuickDeal: Boolean = false
    private var projectId: Int? = null
    private var project: Project? = null
    private var deal: Deal? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private var currentDateText: TextView? = null

    private var mapImages: HashMap<Int, String> = HashMap()
    private var currentType: ReservationImageType? = null
    private val pricesList = arrayOf(30000000, 50000000, 100000000)
    private var currentPrice: Int? = null
    private var dialog: Dialog? = null

    private var interests: List<InterestGroup> = ArrayList()
    private var selectedInterests: MutableList<Interest> = ArrayList()
    private var interestsRequired: List<InterestGroup> = ArrayList()
    private var selectedGroup: HashMap<String, InterestGroup> = HashMap()

    private var promotions: List<Promotion> = ArrayList()
    private var selectedPromotion: Promotion? = null

    private var numDeals = 1
    private var isCloneDeal = false
    private var isEditDeal = false
    private var isPreviewDeal = false
    private var isPreviewUpdateDeal = false
    private var isShowingDialog = false

    private var newParam: NewReservationParam? = null

    private var indexUploadBankInvoice = 0
    private val arrayBankInvoiceLink: MutableList<String> = ArrayList()
    private var numOfUploaded = 0 //To check how many images are uploaded -> To show edit photo on edit mode

    private var currentPermantCity: City? = null
    private var currentPermantDistrict: District? = null

    private var currentContactCity: City? = null
    private var currentContactDistrict: District? = null
    private var typeAddress: Int = 0 //0 = Permant, 1 = Contact

    private val cities: MutableList<City> = ArrayList()
    private val mapDictrict: HashMap<Int, List<District>> = HashMap()

    private val cityAdapter: CityAdapter = CityAdapter()
    private val districtAdapter: DistrictAdapter = DistrictAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_reservation
    }

    companion object {
        private const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"
        private const val BUNDLE_KEY_IS_QUICK_DEAL = "BUNDLE_KEY_IS_QUICK_DEAL"
        private const val BUNDLE_KEY_PROMOTION = "BUNDLE_KEY_PROMOTION"
        private const val BUNDLE_KEY_DEAL = "BUNDLE_KEY_DEAL"
        private const val BUNDLE_KEY_DEAL_COPY = "BUNDLE_KEY_DEAL_COPY"
        private const val BUNDLE_KEY_DEAL_EDIT = "BUNDLE_KEY_DEAL_EDIT"
        private const val BUNDLE_KEY_PARAM = "BUNDLE_KEY_PARAM"
        private const val BUNDLE_KEY_DEAL_PREVIEW = "BUNDLE_KEY_DEAL_PREVIEW"

        fun start(activity: Activity, project: Project, isQuickDeal: Boolean = true, promotion: Promotion? = null) {
            val intent = Intent(activity, ReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            bundle.putBoolean(BUNDLE_KEY_IS_QUICK_DEAL, isQuickDeal)
            promotion?.let {
                bundle.putSerializable(BUNDLE_KEY_PROMOTION, it)
            }
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_REQUEST)
        }

        fun startConfirmQuickDeal(activity: Activity, deal: Deal) {
            val intent = Intent(activity, ReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            bundle.putBoolean(BUNDLE_KEY_IS_QUICK_DEAL, true)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_REQUEST)
        }

        fun start(activity: Activity, deal: Deal, isEdit: Boolean = false) {
            val intent = Intent(activity, ReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            bundle.putSerializable(BUNDLE_KEY_DEAL_EDIT, isEdit)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_REQUEST)
        }

        fun start(activity: Activity, project: Project, deal: Deal, isEdit: Boolean = false) {
            val intent = Intent(activity, ReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            bundle.putSerializable(BUNDLE_KEY_DEAL_EDIT, isEdit)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_REQUEST)
        }

        fun startCopy(activity: Activity, project: Project?, deal: Deal) {
            val intent = Intent(activity, ReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            bundle.putSerializable(BUNDLE_KEY_DEAL_COPY, true)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_REQUEST)
        }

        fun startPreview(
            activity: Activity,
            project: Project,
            param: NewReservationParam,
            promotion: Promotion?,
            isPreviewUpdate: Boolean = false
        ) {
            val intent = Intent(activity, ReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            promotion?.let {
                bundle.putSerializable(BUNDLE_KEY_PROMOTION, it)
            }
            bundle.putSerializable(BUNDLE_KEY_PARAM, param)
            bundle.putSerializable(BUNDLE_KEY_DEAL_PREVIEW, true)
            bundle.putSerializable(BUNDLE_KEY_DEAL_EDIT, isPreviewUpdate)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_PREVIEW_REQUEST)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        reservation_tbToolbar.nav_tvTitle.text = getString(R.string.reservation)
        reservation_tbToolbar.nav_imgBack.setOnClickListener {
            finish()
        }

        presenter.loadCities()
        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            val project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
            projectId = project.id
            this.project = project
        }

        if (intent.hasExtra(BUNDLE_KEY_IS_QUICK_DEAL)) {
            isQuickDeal = bundle?.getBoolean(BUNDLE_KEY_IS_QUICK_DEAL) ?: false
        }

        if (intent.hasExtra(BUNDLE_KEY_PROMOTION)) {
            selectedPromotion = bundle?.getSerializable(BUNDLE_KEY_PROMOTION) as? Promotion
            bindPromotion()
        }

        if (intent.hasExtra(BUNDLE_KEY_DEAL)) {
            deal = bundle?.getSerializable(BUNDLE_KEY_DEAL) as Deal
            reservation_number_deals.visibility = View.GONE
            projectId = deal!!.product_Id

            if (intent.hasExtra(BUNDLE_KEY_DEAL_COPY)) {
                isCloneDeal = true
                bindingCloneReservation()
            } else {
                bindingReservation()
                selectedPromotion = deal?.deal_Promotion
                bindPromotion()
                reservation_header_btn_select_promotion.visibility = View.GONE
                reservation_edit_text_selected_promotion_name.hint =
                    getString(R.string.project_detail_promotion_none)
            }

            if (intent.hasExtra(BUNDLE_KEY_DEAL_EDIT)) {
                isEditDeal = bundle.getSerializable(BUNDLE_KEY_DEAL_EDIT) as Boolean
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_DEAL_PREVIEW)) {
            isPreviewDeal = bundle?.getSerializable(BUNDLE_KEY_DEAL_PREVIEW) as Boolean
            if (intent.hasExtra(BUNDLE_KEY_PARAM)) {
                newParam = bundle.getSerializable(BUNDLE_KEY_PARAM) as NewReservationParam
            }

            if (intent.hasExtra(BUNDLE_KEY_DEAL_EDIT)) {
                isPreviewUpdateDeal = bundle.getSerializable(BUNDLE_KEY_DEAL_EDIT) as Boolean
            }
        }

        initDatePicker()
        initCollapsible()
        initPaymentMethod()
        initPickNumsDeals()
        initHoKhauKhachHang()
        initCMND()
        initBankInvoiceImages()
        initTextChanged()
        initCityDistrict()

        projectId?.let {
            presenter.interests(it)
            presenter.loadPromotions(it)

            if (project == null) {
                reservation_progress.visibility = View.VISIBLE
                presenter.loadProjectDetail(it)
            }
        }

        if (!isCloneDeal && deal != null) {
            reservation_btn_reservation_now.text = getString(R.string.reservation_update)
        }

        if (isPreviewDeal) {
            reservation_tbToolbar.nav_tvTitle.text = getString(R.string.reservation_confirmation)
            reservation_btn_reservation_now.text = getString(R.string.reservation_now)
            bindingPreviewDeal()
        }

        if (deal != null && deal!!.getApproveStatus() == ApproveStatus.Waiting && isEditDeal) {
            reservation_btn_reservation_now.text = getString(R.string.reservation_update)
        }

        if (isEditDeal) {
            reservation_ed_full_name.isEnabled = false
            reservation_view_payment.isEnabled = false
        }

        reservation_btn_reservation_now.setOnClickListener {
            if (isPreviewDeal) {
                submitReservation()
                return@setOnClickListener
            }
            reservationNow()
        }

        project?.let {
            if (it.depositPrice() > 0 && newParam == null && deal == null) {
                selectPrice(it.depositPrice())
            }
        }

        reservation_ed_money_input.setOnClickListener {
            project?.let {
                if (it.depositPrice() > 0) {
                    return@setOnClickListener
                }

                dialog = DialogBottomSheetUtil.showDialogListMoney(this, currentPrice,
                    pricesList.toMutableList(),
                    object : ItemMoneyBottomHandler {
                        override fun onClicked(value: Int) {
                            selectPrice(value)
                        }
                    })
            }
        }

        reservation_header_btn_select_interest.setOnClickListener {
            val dialog = DialogSelectInterest(this@ReservationActivity,
                interests, interestsRequired, selectedInterests, selectedGroup.values.toList(),
                object : InterestDialogHandler {
                    override fun onSelected(
                        interests: List<Interest>,
                        selectedGroup: HashMap<String, InterestGroup>
                    ) {
                        selectedInterests = interests.toMutableList()
                        this@ReservationActivity.selectedGroup = selectedGroup
                        val selectedInterests = getString(R.string.reservation_select_require) +
                                if (selectedInterests.isEmpty()) "" else " (${selectedInterests.size})"
                        reservation_header_btn_select_interest.text = selectedInterests
                        reservation_header_product_info.setBackgroundColor(
                            Util.getColor(
                                this@ReservationActivity,
                                R.color.white
                            )
                        )
                    }
                })
            dialog.showDialog()
        }

        reservation_header_btn_select_promotion.setOnClickListener {
            if (selectedPromotion == null) {
                val dialog = DialogSelectPromotion(
                    this@ReservationActivity,
                    promotions,
                    object : PromotionDialogHandler {
                        override fun onSelected(promotion: Promotion) {
                            selectedPromotion = promotion
                            bindPromotion()
                        }
                    })

                dialog.showDialog()
            } else {
                selectedPromotion = null
                bindPromotion()
            }
        }

        reservation_btn_reservation_now.visibility = View.VISIBLE
        reservation_view_confirmation_buttons.visibility = View.GONE

        if (isQuickDeal) {
            makeSimpleView()
            fillQuickDealInfo()
        }

        setGAScreenName("Reservation - Project Id: $projectId", GACategory.NOTIFICATION)
    }

    private fun makeSimpleView() {
        arrayOf(
            reservation_number_deals,
            project_booking_promotion,
            reservation_view_birthday,
            reservation_view_issued_date,
            reservation_view_issued_place,
            reservation_view_contact_info_title,
            reservation_view_permanent_address,
            reservation_view_permanent,
            reservation_view_contact_address,
            reservation_view_contact,
            reservation_view_email,
            reservation_view_contact_address_separator,
            reservation_view_product_info,
            reservation_view_payment,
            reservation_view_cmnd,
            reservation_view_hokhau
        ).forEach {
            it.visibility = View.GONE
        }
    }

    private fun fillQuickDealInfo() {
        deal?.let {
            reservation_ed_full_name.setText(it.customer_Name_Exact)
            reservation_ed_identity.setText(it.customer_CMND)
            reservation_ed_phone.setText(it.customer_Phone)
            reservation_ed_money_input.text = it.deal_Price_String

            reservation_btn_quick_update_info.setOnClickListener { _ ->
                startUpdatingDeal(it)
            }

            reservation_btn_quick_skip.setOnClickListener { _ ->
                reviewDeal(it)
            }
        }

        if (deal != null) {
            reservation_ed_full_name.isEnabled = false
            reservation_ed_identity.isEnabled = false
            reservation_ed_phone.isEnabled = false
            reservation_ed_money_input.isEnabled = false

            reservation_btn_reservation_now.visibility = View.GONE
            reservation_view_confirmation_buttons.visibility = View.VISIBLE
        }
    }

    private fun startUpdatingDeal(deal: Deal) {
        finish()
        start(this, deal, isEdit = true)
    }

    private fun reviewDeal(deal: Deal) {
        finish()
        ReviewDealActivity.start(this, deal)
    }

    private fun bindingPreviewDeal() {
        reservation_number_deals.visibility = View.GONE
        reservation_header_btn_select_promotion.visibility = View.GONE

        reservation_edit_text_selected_promotion_name.setText(selectedPromotion?.name ?: "")

        val arrayEditText = arrayOf(
            reservation_ed_full_name,
            reservation_ed_identity,
            reservation_ed_issued_place,
            reservation_ed_permanent_address,
            reservation_ed_contact_address,
            reservation_ed_phone,
            reservation_ed_email,
            reservation_et_product_info
        )
        arrayEditText.forEach {
            it.isEnabled = false
        }

        newParam?.let {
            reservation_ed_full_name.setText(it.customer_name)
            reservation_tv_birthday.text = it.customer_birthday
            reservation_ed_identity.setText(it.customer_cmnd)
            reservation_tv_issued_date.text = it.customer_cmnd_date
            reservation_ed_issued_place.setText(it.customer_cmnd_place)

            val permanentAddress =
                it.customer_address_permanent + "\n" + it.customer_address_permanent_district +
                        ", " + it.customer_address_permanent_city
            val contactAddress = it.customer_address + "\n" + it.customer_address_district +
                    ", " + it.customer_address_city

            reservation_ed_permanent_address.setText(permanentAddress)
            reservation_ed_contact_address.setText(contactAddress)

            reservation_ed_phone.setText(it.customer_phone)
            reservation_ed_email.setText(it.customer_email)

            it.deal_price?.let { price ->
                val money = price.toFullPriceString() + " " + getString(R.string.vnd)
                currentPrice = it.deal_price
                reservation_ed_money_input.text = money
            }

            reservation_ed_money_input.setTextColor(Util.getColor(this, R.color.color_black))

            reservation_et_product_info.setText(it.customer_interested)

            selectedInterests.clear()
//            for (interestGroup in it.deal_Interests) {
//                interestGroup.items.firstOrNull()?.let {
//                    selectedInterests.add(it)
//                }
//                selectedGroup[interestGroup.group] = interestGroup
//            }
            val selectedInterests = getString(R.string.reservation_select_require) +
                    if (selectedInterests.isEmpty()) "" else " (${selectedInterests.size})"
            reservation_header_btn_select_interest.text = selectedInterests
            reservation_header_btn_select_interest.visibility = View.GONE

            val isTransfer =
                (it.customer_payment_method == "transfer" || it.customer_payment_method == "cash-transfer")
            reservation_cb_payment_banking.setImageResource(if (isTransfer) R.drawable.check else R.drawable.uncheck)
            reservation_view_payment_banking.visibility =
                if (isTransfer) View.VISIBLE else View.GONE

            val isCash =
                (it.customer_payment_method == "cash" || it.customer_payment_method == "cash-transfer")
            reservation_cb_payment_cash.setImageResource(if (isCash) R.drawable.check else R.drawable.uncheck)
            reservation_view_payment_cash.visibility = if (isCash) View.VISIBLE else View.GONE

            val invoiceDoc: MutableList<DocumentParam> = ArrayList()
            var cashInvoice: DocumentParam? = null
            var cmndDoc: DocumentParam? = null
            var cmndBackDoc: DocumentParam? = null
            var trangBiaDoc: DocumentParam? = null
            var trangChuHoDoc: DocumentParam? = null
            var trangKhachDoc: DocumentParam? = null
            it.upload_files.forEach { doc ->
                when (doc.document_type_id) {
                    DocumentType.INVOICE.value -> invoiceDoc.add(doc)
                    DocumentType.CMND.value -> cmndDoc = doc
                    DocumentType.CMND_BACK.value -> cmndBackDoc = doc
                    DocumentType.hoKhau_TrangBia.value -> trangBiaDoc = doc
                    DocumentType.hoKhau_TrangChuHo.value -> trangChuHoDoc = doc
                    DocumentType.hoKhau_TrangKhach.value -> trangKhachDoc = doc
                    DocumentType.cashInvoice.value -> cashInvoice = doc
                }
            }

            if (trangBiaDoc == null && trangChuHoDoc == null && trangKhachDoc == null) {
                reservation_view_trang_bia.visibility = View.INVISIBLE
                reservation_view_trang_chu_ho.visibility = View.INVISIBLE
                reservation_view_trang_khach_hang.visibility = View.INVISIBLE
            } else {
                val arrayDocHoKhau = arrayOf(trangBiaDoc, trangChuHoDoc, trangKhachDoc)
                val arrayViewHoKhau = arrayOf(
                    reservation_view_trang_bia, reservation_view_trang_chu_ho,
                    reservation_view_trang_khach_hang
                )
                val arrayTvHoKhau = arrayOf(
                    reservation_tv_trang_bia, reservation_tv_trang_chu_ho,
                    reservation_tv_trang_khach_hang
                )
                val arrayImgHoKhau = arrayOf(
                    reservation_img_trang_bia, reservation_img_trang_chu_ho,
                    reservation_img_trang_khach_hang
                )
                val arrayIndexOnMap = arrayOf(
                    ReservationImageType.HoKhau_TrangBia.value,
                    ReservationImageType.HoKhau_TrangChuHo.value,
                    ReservationImageType.HoKhau_TrangKhach.value
                )

                arrayDocHoKhau.forEachIndexed { index, documentParam ->
                    if (documentParam == null) {
                        arrayViewHoKhau[index].visibility = View.INVISIBLE
                    } else {
                        arrayTvHoKhau[index].visibility = View.GONE
                        arrayImgHoKhau[index].visibility = View.VISIBLE
                        LHPicasso.loadImage(documentParam.link.getLink(), arrayImgHoKhau[index])
                        mapImages[arrayIndexOnMap[index]] = documentParam.link
                    }
                }
            }

            cmndDoc?.let { doc ->
                reservation_tv_cmnd.visibility = View.GONE
                reservation_img_cmnd.visibility = View.VISIBLE
                LHPicasso.loadImage(doc.link.getLink(), reservation_img_cmnd)
                mapImages[ReservationImageType.Cmnd.value] = doc.link
            }

            cmndBackDoc?.let { doc ->
                reservation_tv_back_cmnd.visibility = View.GONE
                reservation_img_back_cmnd.visibility = View.VISIBLE
                LHPicasso.loadImage(doc.link.getLink(), reservation_img_back_cmnd)
                mapImages[ReservationImageType.CmndBack.value] = doc.link
            }

            if (isTransfer) {
                val images: MutableList<BitmapUpload> = ArrayList()
                invoiceDoc.forEach { doc ->
                    val bitmap = BitmapUpload()
                    bitmap.url = doc.link
                    bitmap.time = System.currentTimeMillis()
                    bitmap.uploaded = true
                    images.add(bitmap)
                }
                bindingBankInvoiceImages(images)
            }

            if (isCash) {
                reservation_view_cash_invoice.visibility =
                    if (cashInvoice == null) View.INVISIBLE else View.VISIBLE

                cashInvoice?.let { doc ->
                    reservation_tv_cash_invoice.visibility = View.GONE
                    reservation_img_cash_invoice.visibility = View.VISIBLE
                    LHPicasso.loadImage(doc.link.getLink(), reservation_img_cash_invoice)
                    mapImages[ReservationImageType.CashInvoice.value] = doc.link
                }
            }
        }
    }

    private fun initCityDistrict() {
        if (isPreviewDeal) {
            reservation_view_permanent.visibility = View.GONE
            reservation_view_contact.visibility = View.GONE
            return
        }

        arrayOf(
            reservation_tv_permanent_city,
            reservation_tv_contact_city
        ).forEachIndexed { index, textView ->
            textView.setOnClickListener {
                typeAddress = index
                showSelectCity()
            }
        }

        arrayOf(
            reservation_tv_permanent_district,
            reservation_tv_contact_district
        ).forEachIndexed { index, textView ->
            textView.setOnClickListener {
                typeAddress = index
                showSelectDistrict()
            }
        }
    }

    private fun bindPromotion() {
        val name = selectedPromotion?.name ?: ""
        reservation_edit_text_selected_promotion_name.setText(name)

        val isNoPromotion = selectedPromotion == null
        reservation_header_btn_select_promotion.setBackgroundResource(if (isNoPromotion) R.drawable.background_border_dark_corner_5 else R.drawable.background_black_border_round_5)
        reservation_header_btn_select_promotion.setTextColor(
            Util.getColor(
                this,
                if (!isNoPromotion) R.color.white else R.color.black
            )
        )
        reservation_header_btn_select_promotion.text =
            getString(if (isNoPromotion) R.string.project_detail_select_promotion else R.string.project_detail_unselect_promotion)
    }

    private fun bindingBankInvoiceImages(images: MutableList<BitmapUpload>) {
        val arrayTvBankInvoice = arrayOf(
            reservation_tv_payment_banking01,
            reservation_tv_payment_banking02,
            reservation_tv_payment_banking03,
            reservation_tv_payment_banking04,
            reservation_tv_payment_banking05
        )
        val arrayImagesBankInvoice = arrayOf(
            reservation_img_payment_banking01,
            reservation_img_payment_banking02,
            reservation_img_payment_banking03,
            reservation_img_payment_banking04,
            reservation_img_payment_banking05
        )

        if (isPreviewDeal) {
            arrayTvBankInvoice.forEach {
                it.visibility = View.GONE
            }
        }

        numOfUploaded = images.count()
        for (index in 0 until images.count()) {
            if (index < arrayTvBankInvoice.size) {
                arrayTvBankInvoice[index].visibility = View.GONE
                arrayImagesBankInvoice[index].visibility = View.VISIBLE
                LHPicasso.loadImage(images[index].url.getLink(), arrayImagesBankInvoice[index])
                arrayBankInvoiceLink.add(images[index].url)
            }
        }
    }

    private fun initTextChanged() {
        val arrayEditText = arrayOf(
            reservation_ed_full_name, reservation_ed_identity,
            reservation_ed_issued_place, reservation_ed_permanent_address,
            reservation_ed_contact_address, reservation_ed_phone, reservation_ed_email
        )
        val arrayView = arrayOf(
            reservation_view_full_name, reservation_view_identity,
            reservation_view_issued_place, reservation_view_permanent_address,
            reservation_view_contact_address, reservation_view_phone, reservation_view_email
        )

        arrayEditText.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    if (text.isNotEmpty()) {
                        arrayView[index].setBackgroundColor(
                            Util.getColor(
                                this@ReservationActivity,
                                R.color.white
                            )
                        )
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }

    private fun initBankInvoiceImages() {
        val arrayTvBankInvoice = arrayOf(
            reservation_tv_payment_banking01,
            reservation_tv_payment_banking02,
            reservation_tv_payment_banking03,
            reservation_tv_payment_banking04,
            reservation_tv_payment_banking05
        )
        val arrayImagesBankInvoice = arrayOf(
            reservation_img_payment_banking01,
            reservation_img_payment_banking02,
            reservation_img_payment_banking03,
            reservation_img_payment_banking04,
            reservation_img_payment_banking05
        )

        arrayTvBankInvoice.forEachIndexed { index, view ->
            val param = view.layoutParams
            val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(this, 50)) / 3
            param.height = heightView
            param.width = heightView
            view.layoutParams = param

            val paramImg = arrayImagesBankInvoice[index].layoutParams
            paramImg.height = heightView
            paramImg.width = heightView
            arrayImagesBankInvoice[index].layoutParams = paramImg

            view.setOnClickListener {
                if (isPreviewDeal) {
                    return@setOnClickListener
                }

                indexUploadBankInvoice = index
                currentType = ReservationImageType.BankInvoice
                showImagePicker(Constant.UPLOAD_IMAGE_INVOICE_REQUEST)
            }
        }

        arrayImagesBankInvoice.forEachIndexed { index, view ->
            view.setOnClickListener {
                if ((isEditDeal && index < numOfUploaded) || isPreviewDeal) {
                    return@setOnClickListener
                }
                indexUploadBankInvoice = index
                currentType = ReservationImageType.BankInvoice
                showImagePicker(Constant.UPLOAD_IMAGE_INVOICE_REQUEST)
                reservation_view_cmnd_header.setTextColor(Util.getColor(this, R.color.color_black))
            }
        }
    }

    private fun showImagePicker(requestCode: Int) {
        if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionWriteExternalStorage(
                this
            ) &&
            Permission.checkPermissionCamera(this)
        ) {
            BitmapUtil.openPickImage(this, requestCode)
        }
    }

    override fun bindingProjectDetail(project: Project) {
        reservation_progress.visibility = View.GONE
        this.project = project
    }

    override fun bindingProjectDetailFailed() {
        reservation_progress.visibility = View.GONE
    }

    private fun initCMND() {
        val arrayCmndTv = arrayOf(reservation_tv_cmnd, reservation_tv_back_cmnd)
        val arrayCmndImg = arrayOf(reservation_img_cmnd, reservation_img_back_cmnd)

        arrayCmndTv.forEachIndexed { index, view ->
            val param = view.layoutParams
            val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(this, 50)) / 3
            param.height = heightView
            view.layoutParams = param

            val paramImg = arrayCmndImg[index].layoutParams
            paramImg.height = heightView
            arrayCmndImg[index].layoutParams = paramImg

            view.setOnClickListener {
                if (isPreviewDeal) {
                    return@setOnClickListener
                }
                currentType = ReservationImageType.from(index + 3)
                showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)

                reservation_view_cmnd_header.setTextColor(Util.getColor(this, R.color.color_black))
            }
        }

        arrayCmndImg.forEachIndexed { index, view ->
            view.setOnClickListener {
                if (isPreviewDeal) {
                    return@setOnClickListener
                }
                currentType = ReservationImageType.from(index + 3)
                showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)
                reservation_view_cmnd_header.setTextColor(Util.getColor(this, R.color.color_black))
            }
        }
    }

    private fun initHoKhauKhachHang() {
        val arrayHoKhauTv = arrayOf(
            reservation_tv_trang_bia, reservation_tv_trang_chu_ho,
            reservation_tv_trang_khach_hang
        )
        val arrayHoKhauImg = arrayOf(
            reservation_img_trang_bia, reservation_img_trang_chu_ho,
            reservation_img_trang_khach_hang
        )

        arrayHoKhauTv.forEachIndexed { index, view ->
            val param = view.layoutParams
            val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(this, 50)) / 3
            param.height = heightView
            view.layoutParams = param

            val paramImg = arrayHoKhauImg[index].layoutParams
            paramImg.height = heightView
            arrayHoKhauImg[index].layoutParams = paramImg

            view.setOnClickListener {
                if (isPreviewDeal) {
                    return@setOnClickListener
                }
                currentType = ReservationImageType.from(index + 5)
                showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)
            }
        }

        arrayHoKhauImg.forEachIndexed { index, view ->
            view.setOnClickListener {
                if (isPreviewDeal) {
                    return@setOnClickListener
                }
                currentType = ReservationImageType.from(index + 5)
                showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)
            }
        }
    }

    private fun initPickNumsDeals() {
        updateNumDeals()
        reservation_btn_subtract_deals.setOnClickListener {
            if (numDeals > 1) {
                numDeals--
                updateNumDeals()
            }
        }
        reservation_btn_plus_deals.setOnClickListener {
            numDeals++
            updateNumDeals()
        }
    }

    private fun updateNumDeals() {
        reservation_tv_num_deals.text = numDeals.toString()
        reservation_btn_subtract_deals.isEnabled = numDeals > 1
        reservation_btn_subtract_deals.setTextColor(
            if (numDeals > 1)
                StringUtil.getColor(this, R.color.white) else
                StringUtil.getColor(this, R.color.color_black_50)
        )
    }

    override fun bindingInterests(list: List<InterestGroup>) {
        interests = list
        interestsRequired = interests.filter { it.required }
    }

    override fun bindingPromotions(list: List<Promotion>) {
        this.promotions = list
    }

    private fun selectPrice(value: Int) {
        currentPrice = value
        val money = value.toFullPriceString() + " " + getString(R.string.vnd)
        reservation_ed_money_input.text = money
        reservation_ed_money_input.setTextColor(Util.getColor(this, R.color.color_black))
        reservation_view_money_input.setBackgroundColor(
            Util.getColor(
                this@ReservationActivity,
                R.color.white
            )
        )
        dialog?.dismiss()
    }

    private fun bindingCloneReservation() {
        deal?.let { it ->
            reservation_ed_full_name.setText(it.customer_Name_Exact)
            reservation_tv_birthday.text = it.customer_Birthday
            reservation_ed_identity.setText(it.customer_CMND)
            reservation_tv_issued_date.text = it.customer_CMND_Date
            reservation_ed_issued_place.setText(it.customer_CMND_Place)

            reservation_ed_permanent_address.setText(it.customer_Address_Permanent)
            reservation_ed_contact_address.setText(it.customer_Address)
            reservation_ed_phone.setText(it.customer_Phone)
            reservation_ed_email.setText(it.customer_Email)

            val money = it.booking_Price.toInt().toFullPriceString() + " " + getString(R.string.vnd)
            currentPrice = it.booking_Price.toInt()
            reservation_ed_money_input.text = money
            reservation_ed_money_input.setTextColor(Util.getColor(this, R.color.color_black))

            reservation_et_product_info.setText(it.customer_Interested)

            selectedInterests.clear()
            for (interestGroup in it.deal_Interests) {
                interestGroup.items.firstOrNull()?.let {
                    selectedInterests.add(it)
                }
                selectedGroup[interestGroup.group] = interestGroup
            }
            val selectedInterests = getString(R.string.reservation_select_require) +
                    if (selectedInterests.isEmpty()) "" else " (${selectedInterests.size})"
            reservation_header_btn_select_interest.text = selectedInterests
        }
    }

    private fun findCityIdByName(name: String): Int {
        if (cities.isEmpty()) {
            return -1
        }
        val city = cities.firstOrNull { it.city_Name == name }
        city?.let {
            return it.id
        }
        return -1
    }

    private fun bindingReservation() {
        deal?.let { it ->
            reservation_ed_full_name.setText(it.customer_Name_Exact)
            reservation_tv_birthday.text = it.customer_Birthday
            reservation_ed_identity.setText(it.customer_CMND)
            reservation_tv_issued_date.text = it.customer_CMND_Date
            reservation_ed_issued_place.setText(it.customer_CMND_Place)

            currentPermantCity = City()
            if (!it.customer_Address_Permanent_City.isNullOrEmpty()) {
                currentPermantCity!!.city_Name = it.customer_Address_Permanent_City
                currentPermantCity!!.id = findCityIdByName(it.customer_Address_Permanent_City)
            }

            currentPermantDistrict = District()
            if (!it.customer_Address_Permanent_District.isNullOrEmpty()) {
                currentPermantDistrict!!.id = -1
                currentPermantDistrict!!.district_Name = it.customer_Address_Permanent_District
            }

            currentContactCity = City()
            if (!it.customer_Address_City.isNullOrEmpty()) {
                currentContactCity!!.city_Name = it.customer_Address_City
                currentContactCity!!.id = findCityIdByName(it.customer_Address_City)
            }

            currentContactDistrict = District()
            if (!it.customer_Address_District.isNullOrEmpty()) {
                currentContactDistrict!!.id = -1
                currentContactDistrict!!.district_Name = it.customer_Address_District
            }

            reservation_ed_permanent_address.setText(it.customer_Address_Permanent)
            reservation_ed_contact_address.setText(it.customer_Address)

            val arrayAddress = arrayOf(
                currentPermantDistrict!!.district_Name, currentPermantCity!!.city_Name,
                currentContactDistrict!!.district_Name, currentContactCity!!.city_Name
            )

            arrayOf(
                reservation_tv_permanent_district,
                reservation_tv_permanent_city,
                reservation_tv_contact_district,
                reservation_tv_contact_city
            ).forEachIndexed { index, textView ->
                textView.text = arrayAddress[index]
                textView.setTextColor(Util.getColor(this, R.color.color_black))
            }

            reservation_ed_phone.setText(it.customer_Phone)
            reservation_ed_email.setText(it.customer_Email)

            selectedInterests.clear()
            for (interestGroup in it.deal_Interests) {
                interestGroup.items.firstOrNull()?.let {
                    selectedInterests.add(it)
                }
                selectedGroup[interestGroup.group] = interestGroup
            }
            val selectedInterests = getString(R.string.reservation_select_require) +
                    if (selectedInterests.isEmpty()) "" else " (${selectedInterests.size})"
            reservation_header_btn_select_interest.text = selectedInterests

            val money = it.booking_Price.toInt().toFullPriceString() + " " + getString(R.string.vnd)
            reservation_ed_money_input.text = money
            currentPrice = it.booking_Price.toInt()
            reservation_ed_money_input.setTextColor(Util.getColor(this, R.color.color_black))

            reservation_et_product_info.setText(it.customer_Interested)

            val isTransfer =
                (it.customer_Payment_Method == "transfer" || it.customer_Payment_Method == "cash-transfer")
            reservation_cb_payment_banking.setImageResource(if (isTransfer) R.drawable.check else R.drawable.uncheck)
            reservation_view_payment_banking.visibility =
                if (isTransfer) View.VISIBLE else View.GONE

            val isCash =
                (it.customer_Payment_Method == "cash" || it.customer_Payment_Method == "cash-transfer")
            reservation_cb_payment_cash.setImageResource(if (isCash) R.drawable.check else R.drawable.uncheck)
            reservation_view_payment_cash.visibility = if (isCash) View.VISIBLE else View.GONE

            val invoiceDoc: MutableList<Document> = ArrayList()
            var cashInvoice: Document? = null
            var cmndDoc: Document? = null
            var cmndBackDoc: Document? = null
            var trangBiaDoc: Document? = null
            var trangChuHoDoc: Document? = null
            var trangKhachDoc: Document? = null
            it.documents.forEach {
                when (it.document_Type_Id) {
                    DocumentType.INVOICE.value -> invoiceDoc.add(it)
                    DocumentType.CMND.value -> cmndDoc = it
                    DocumentType.CMND_BACK.value -> cmndBackDoc = it
                    DocumentType.hoKhau_TrangBia.value -> trangBiaDoc = it
                    DocumentType.hoKhau_TrangChuHo.value -> trangChuHoDoc = it
                    DocumentType.hoKhau_TrangKhach.value -> trangKhachDoc = it
                    DocumentType.cashInvoice.value -> cashInvoice = it
                }
            }

            trangBiaDoc?.let {
                reservation_tv_trang_bia.visibility = View.GONE
                reservation_img_trang_bia.visibility = View.VISIBLE
                LHPicasso.loadImage(it.link.getLink(), reservation_img_trang_bia)
                mapImages[ReservationImageType.HoKhau_TrangBia.value] = it.link
            }

            trangChuHoDoc?.let {
                reservation_tv_trang_chu_ho.visibility = View.GONE
                reservation_img_trang_chu_ho.visibility = View.VISIBLE
                LHPicasso.loadImage(it.link.getLink(), reservation_img_trang_chu_ho)
                mapImages[ReservationImageType.HoKhau_TrangChuHo.value] = it.link
            }

            trangKhachDoc?.let {
                reservation_tv_trang_khach_hang.visibility = View.GONE
                reservation_img_trang_khach_hang.visibility = View.VISIBLE
                LHPicasso.loadImage(it.link.getLink(), reservation_img_trang_khach_hang)
                mapImages[ReservationImageType.HoKhau_TrangKhach.value] = it.link
            }

            cmndDoc?.let {
                reservation_tv_cmnd.visibility = View.GONE
                reservation_img_cmnd.visibility = View.VISIBLE
                LHPicasso.loadImage(it.link.getLink(), reservation_img_cmnd)
                mapImages[ReservationImageType.Cmnd.value] = it.link
            }

            cmndBackDoc?.let {
                reservation_tv_back_cmnd.visibility = View.GONE
                reservation_img_back_cmnd.visibility = View.VISIBLE
                LHPicasso.loadImage(it.link.getLink(), reservation_img_back_cmnd)
                mapImages[ReservationImageType.CmndBack.value] = it.link
            }

            if (isTransfer) {
                val images: MutableList<BitmapUpload> = ArrayList()
                invoiceDoc.forEach { doc ->
                    val bitmap = BitmapUpload()
                    bitmap.url = doc.link
                    bitmap.time = System.currentTimeMillis()
                    bitmap.uploaded = true
                    images.add(bitmap)
                }
                bindingBankInvoiceImages(images)
            }

            if (isCash) {
                cashInvoice?.let {
                    reservation_tv_cash_invoice.visibility = View.GONE
                    reservation_img_cash_invoice.visibility = View.VISIBLE
                    LHPicasso.loadImage(it.link.getLink(), reservation_img_cash_invoice)
                    mapImages[ReservationImageType.CashInvoice.value] = it.link
                    numOfUploaded = 1
                }
            }
        }

    }

    private fun submitReservation() {
        reservation_progress.visibility = View.VISIBLE
        if (isPreviewUpdateDeal) {
            newParam?.let {
                presenter.update(it)
            }
        } else {
            newParam?.let {
                if (isQuickDeal) {
                    presenter.newSimple(it)
                } else {
                    presenter.new(it)
                }
            }
        }
    }

    private fun reservationNow() {
        val reservationParam = NewReservationParam()
        reservationParam.product_id = projectId!!

        val name = reservation_ed_full_name.text.toString()

        var isInputValid = true
        isShowingDialog = false
        var view: View? = null

        if (name.isEmpty()) {
            isInputValid = false

            reservation_view_full_name.setBackgroundColor(Util.getColor(this, R.color.color_red_10))
            view = reservation_view_full_name
            if (!isShowingDialog) {
                DialogUtil.showErrorDialog(
                    this, getString(R.string.error_name_empty_title),
                    getString(R.string.error_name_empty_desc), getString(R.string.ok), null
                )
                isShowingDialog = true
            }
        }

        if (isQuickDeal) {
            if (reservation_ed_identity.text.toString().isEmpty()) {
                isInputValid = false
                reservation_view_identity.setBackgroundColor(Util.getColor(this, R.color.color_red_10))

                if (view == null) {
                    view = reservation_view_identity
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_phone_invalid_title),
                        getString(R.string.error_phone_invalid_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }

            if (!reservation_ed_phone.text.toString().isValidPhoneNumber()) {
                isInputValid = false
                reservation_view_phone.setBackgroundColor(Util.getColor(this, R.color.color_red_10))

                if (view == null) {
                    view = reservation_view_phone
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_phone_invalid_title),
                        getString(R.string.error_phone_invalid_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }

            if (currentPrice == null || currentPrice == 0) {
                isInputValid = false
                reservation_view_money_input.setBackgroundColor(
                    Util.getColor(
                        this,
                        R.color.color_red_10
                    )
                )

                if (view == null) {
                    view = reservation_view_price
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_money_invalid_title),
                        getString(R.string.error_money_invalid_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }

            if (!isInputValid) {
                return
            }

            reservationParam.customer_name = name
            reservationParam.customer_cmnd = reservation_ed_identity.text.toString()
            reservationParam.customer_phone = reservation_ed_phone.text.toString()
            reservationParam.number_of_flat = 1
            currentPrice?.let { price ->
                if (price > 0) {
                    reservationParam.deal_price = price
                }
            }

            presenter.newSimple(reservationParam)
            return
        }

        val isRequireInfo = if (project != null) project!!.is_required_deal_info else false

        if (isRequireInfo) {
            val arrayCusInfoView = arrayOf(
                reservation_view_birthday, reservation_view_identity,
                reservation_view_issued_date, reservation_view_issued_place
            )
            val arrayCusInfoTv = arrayOf(
                reservation_tv_birthday, reservation_ed_identity, reservation_tv_issued_date,
                reservation_ed_issued_place
            )

            arrayCusInfoTv.forEachIndexed { index, tv ->
                if (tv.text.isEmpty()) {
                    isInputValid = false
                    arrayCusInfoView[index].setBackgroundColor(
                        Util.getColor(
                            this,
                            R.color.color_red_10
                        )
                    )
                    if (view == null) {
                        view = arrayCusInfoView[index]
                    }
                    if (!isShowingDialog) {
                        DialogUtil.showErrorDialog(
                            this,
                            getString(R.string.error_customer_info_empty_title),
                            getString(R.string.error_customer_info_empty_desc),
                            getString(R.string.ok),
                            null
                        )
                        isShowingDialog = true
                    }
                }
            }

            val arrayCusAddressTv = arrayOf(
                reservation_ed_permanent_address, reservation_ed_contact_address,
                reservation_ed_phone
            )
            val arrayCusAddressView = arrayOf(
                reservation_view_permanent_address, reservation_view_contact_address,
                reservation_view_phone
            )

            arrayCusAddressTv.forEachIndexed { index, tv ->
                if (tv.text.isEmpty()) {
                    isInputValid = false
                    arrayCusAddressView[index].setBackgroundColor(
                        Util.getColor(
                            this,
                            R.color.color_red_10
                        )
                    )

                    if (view == null) {
                        view = arrayCusAddressView[index]
                    }
                    if (!isShowingDialog) {
                        DialogUtil.showErrorDialog(
                            this,
                            getString(R.string.error_customer_contact_empty_title),
                            getString(R.string.error_customer_contact_empty_desc),
                            getString(R.string.ok),
                            null
                        )
                        isShowingDialog = true
                    }
                }
            }

            if (currentPermantCity == null || currentPermantDistrict == null) {
                isInputValid = false
                reservation_view_permanent.setBackgroundColor(
                    Util.getColor(
                        this,
                        R.color.color_red_10
                    )
                )

                if (view == null) {
                    view = reservation_view_permanent
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this,
                        getString(R.string.error_customer_contact_empty_title),
                        getString(R.string.error_customer_contact_empty_desc),
                        getString(R.string.ok),
                        null
                    )
                    isShowingDialog = true
                }
            }

            if (currentContactCity == null || currentContactDistrict == null) {
                isInputValid = false
                reservation_view_contact.setBackgroundColor(
                    Util.getColor(
                        this,
                        R.color.color_red_10
                    )
                )

                if (view == null) {
                    view = reservation_view_contact
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this,
                        getString(R.string.error_customer_contact_empty_title),
                        getString(R.string.error_customer_contact_empty_desc),
                        getString(R.string.ok),
                        null
                    )
                    isShowingDialog = true
                }
            }

            if (!reservation_ed_phone.text.toString().isValidPhoneNumber()) {
                isInputValid = false
                reservation_view_phone.setBackgroundColor(Util.getColor(this, R.color.color_red_10))

                if (view == null) {
                    view = reservation_view_phone
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_phone_invalid_title),
                        getString(R.string.error_phone_invalid_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }

            val email = reservation_ed_email.text.toString()
            reservation_view_email.setBackgroundColor(Util.getColor(this, R.color.white))
            if (!email.isValidEmail()) {
                isInputValid = false
                reservation_view_email.setBackgroundColor(Util.getColor(this, R.color.color_red_10))

                if (view == null) {
                    view = reservation_view_email
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_email_invalid_title),
                        getString(R.string.error_email_invalid_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }

            if (currentPrice == null || currentPrice == 0) {
                isInputValid = false
                reservation_view_money_input.setBackgroundColor(
                    Util.getColor(
                        this,
                        R.color.color_red_10
                    )
                )

                if (view == null) {
                    view = reservation_view_price
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_money_invalid_title),
                        getString(R.string.error_money_invalid_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }

            if (reservation_view_payment_banking.visibility == View.GONE && reservation_view_payment_cash.visibility == View.GONE) {
                reservation_tv_payment.setTextColor(Util.getColor(this, R.color.color_red))
                isInputValid = false

                if (view == null) {
                    view = reservation_tv_payment
                }
                if (!isShowingDialog) {
                    DialogUtil.showErrorDialog(
                        this, getString(R.string.error_payment_empty_title),
                        getString(R.string.error_payment_empty_desc), getString(R.string.ok), null
                    )
                    isShowingDialog = true
                }
            }
        }

        var paymentMethod = "cash-transfer"
        if (reservation_view_payment_banking.visibility == View.GONE || reservation_view_payment_cash.visibility == View.GONE) {
            if (reservation_view_payment_banking.visibility == View.VISIBLE) {
                paymentMethod = "transfer"
            } else if (reservation_view_payment_cash.visibility == View.VISIBLE) {
                paymentMethod = "cash"
            }
        }

        reservationParam.customer_payment_method = paymentMethod


        val uploadImages: MutableList<DocumentParam> = ArrayList()
        uploadImages.addAll(getListHoKhauImages())

        val isBankTransfer = reservation_view_payment_banking.visibility == View.VISIBLE
        val uploadInvoiceImages = getListImages(isRequireInfo)
        if (uploadInvoiceImages.isEmpty() && isRequireInfo && isBankTransfer) {
            isInputValid = false
        }
        uploadImages.addAll(uploadInvoiceImages)

        val cmndImages = getListCMNDImages(isRequireInfo)
        if (cmndImages.isEmpty() && isRequireInfo) {
            reservation_view_cmnd_header.setTextColor(Util.getColor(this, R.color.color_red))
            if (view == null) {
                view = reservation_view_cmnd
            }
            isInputValid = false
        }
        uploadImages.addAll(cmndImages)

        reservationParam.upload_files = uploadImages
        reservationParam.customer_name = name
        reservationParam.customer_cmnd = reservation_ed_identity.text.toString()
        reservationParam.customer_cmnd_date = reservation_tv_issued_date.text.toString()
        reservationParam.customer_birthday = reservation_tv_birthday.text.toString()
        reservationParam.customer_cmnd_place = reservation_ed_issued_place.text.toString()

        reservationParam.customer_address_permanent =
            reservation_ed_permanent_address.text.toString()
        reservationParam.customer_address = reservation_ed_contact_address.text.toString()
        reservationParam.customer_address_permanent_district =
            reservation_tv_permanent_district.text.toString()
        reservationParam.customer_address_permanent_city =
            reservation_tv_permanent_city.text.toString()
        reservationParam.customer_address_district = reservation_tv_contact_district.text.toString()
        reservationParam.customer_address_city = reservation_tv_contact_city.text.toString()

        reservationParam.customer_phone = reservation_ed_phone.text.toString()
        reservationParam.customer_email = reservation_ed_email.text.toString()

        reservationParam.customer_interested = reservation_et_product_info.text.toString()
        reservationParam.promotion_id = selectedPromotion?.id ?: 0

        currentPrice?.let { price ->
            if (price > 0) {
                reservationParam.deal_price = price
            }
        }

        if (deal == null || isCloneDeal) {
            reservationParam.number_of_flat = numDeals
        }

        val interestsParam: MutableList<InterestParam> = ArrayList()
        for (interest in selectedInterests) {
            interestsParam.add(InterestParam(interest))
        }
        reservationParam.deal_interest = interestsParam.toList()

        var isValid = true
        for (requireGroup in interestsRequired) {
            if (!selectedGroup.containsKey(requireGroup.group) && isRequireInfo) {
                isValid = false
                break
            }
        }

        if (!isValid) {
            if (!isShowingDialog) {
                DialogUtil.showErrorDialog(
                    this,
                    getString(R.string.error_missing_info),
                    getString(R.string.error_missing_interest_deal_desc),
                    getString(R.string.ok), null
                )
            }
            reservation_header_product_info.setBackgroundColor(
                Util.getColor(
                    this,
                    R.color.color_red_10
                )
            )
            if (view == null) {
                view = reservation_view_product_info
            }
            isInputValid = false
        }

        if (!isInputValid) {
            view?.let {
                reservation_scroll_view.scrollTo(0, it.y.toInt())
            }
            return
        }

        val isUpdateDeal = deal != null && !isCloneDeal
        if (isUpdateDeal) {
            reservationParam.id = deal!!.id
        }

        project?.let {
            startPreview(this, it, reservationParam, selectedPromotion, isUpdateDeal)
        }
    }

    private fun getListImages(isRequireInfo: Boolean): List<DocumentParam> {
        val isBankTransfer = reservation_view_payment_banking.visibility == View.VISIBLE
        val isCashTransfer = reservation_view_payment_cash.visibility == View.VISIBLE
        val images: MutableList<DocumentParam> = ArrayList()

        if (isBankTransfer) {
            if (arrayBankInvoiceLink.isEmpty() && isRequireInfo) {
                if (!isShowingDialog) {
                    isShowingDialog = true
                    DialogUtil.showErrorDialog(
                        this,
                        getString(R.string.error_image_delegate_empty_title),
                        getString(R.string.error_image_delegate_empty_desc),
                        getString(R.string.ok),
                        null
                    )
                }
                return ArrayList()
            }

            for (item in arrayBankInvoiceLink) {
                val bankBookingInvoiceDoc = DocumentParam()
                bankBookingInvoiceDoc.document_type_id = DocumentType.INVOICE.value
                bankBookingInvoiceDoc.link = item
                images.add(bankBookingInvoiceDoc)
            }
        }

        if (isCashTransfer) {
            val invoice = mapImages[ReservationImageType.CashInvoice.value]
            invoice?.let {
                val bankBookingInvoiceDoc = DocumentParam()
                bankBookingInvoiceDoc.document_type_id = DocumentType.cashInvoice.value
                bankBookingInvoiceDoc.link = invoice
                images.add(bankBookingInvoiceDoc)
            }
        }

        return images.toMutableList()
    }

    private fun getListCMNDImages(isRequireInfo: Boolean): List<DocumentParam> {
        val images: MutableList<DocumentParam> = ArrayList()

        val cmnd = mapImages[ReservationImageType.Cmnd.value]
        val cmndBack = mapImages[ReservationImageType.CmndBack.value]
        if ((cmnd == null || cmndBack == null) && isRequireInfo) {
            if (!isShowingDialog) {
                isShowingDialog = true
                DialogUtil.showErrorDialog(
                    this, getString(R.string.error_images_empty_title),
                    getString(R.string.error_images_empty_desc), getString(R.string.ok), null
                )
            }
            return ArrayList()
        }

        cmnd?.let {
            val cmndDoc = DocumentParam()
            cmndDoc.document_type_id = DocumentType.CMND.value
            cmndDoc.link = cmnd
            images.add(cmndDoc)
        }

        cmndBack?.let {
            val cmndBackDoc = DocumentParam()
            cmndBackDoc.document_type_id = DocumentType.CMND_BACK.value
            cmndBackDoc.link = cmndBack
            images.add(cmndBackDoc)
        }

        return images
    }

    private fun getListHoKhauImages(): List<DocumentParam> {
        val images: MutableList<DocumentParam> = ArrayList()
        val trangbiaImg = mapImages[ReservationImageType.HoKhau_TrangBia.value]
        if (trangbiaImg != null) {
            val document = DocumentParam()
            document.document_type_id = DocumentType.hoKhau_TrangBia.value
            document.link = trangbiaImg
            images.add(document)
        }

        val trangChuHoImg = mapImages[ReservationImageType.HoKhau_TrangChuHo.value]
        if (trangChuHoImg != null) {
            val document = DocumentParam()
            document.document_type_id = DocumentType.hoKhau_TrangChuHo.value
            document.link = trangChuHoImg
            images.add(document)
        }

        val trangKhachImg = mapImages[ReservationImageType.HoKhau_TrangKhach.value]
        if (trangKhachImg != null) {
            val document = DocumentParam()
            document.document_type_id = DocumentType.hoKhau_TrangKhach.value
            document.link = trangKhachImg
            images.add(document)
        }
        return images.toMutableList()
    }

    private fun showSelectCity() {
        val viewContent =
            if (typeAddress == 0) reservation_view_permanent else reservation_view_contact
        viewContent.setBackgroundColor(Util.getColor(this@ReservationActivity, R.color.white))

        val alertDialog = AlertDialog.Builder(getContext()).create()
        val inflater = layoutInflater
        val convertView = inflater.inflate(R.layout.dialog_address, null) as View
        convertView.dialog_address_tv_title.text = getContext().getText(R.string.select_city)

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(getContext())
        convertView.dialog_address_recycleView_city.layoutManager = linearLayoutManager
        convertView.dialog_address_recycleView_city.adapter = cityAdapter

        val districtLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(getContext())
        convertView.dialog_address_recycleView_district.layoutManager = districtLayoutManager
        convertView.dialog_address_recycleView_district.adapter = districtAdapter

        cityAdapter.listener = object : CityItemHandler {
            override fun onClicked(city: City) {
                if (typeAddress == 0) {
                    currentPermantCity = city
                    currentPermantDistrict = null
                } else {
                    currentContactCity = city
                    currentContactDistrict = null
                }

                val tvDistrict =
                    if (typeAddress == 0) reservation_tv_permanent_district else reservation_tv_contact_district
                val districtName = this@ReservationActivity.getString(R.string.select_district)
                tvDistrict.text = districtName

                val colorDark50 = Util.getColor(this@ReservationActivity, R.color.color_black_50)
                tvDistrict.setTextColor(colorDark50)

                if (!mapDictrict.containsKey(city.id)) {
                    presenter.loadDistrict(city.id)
                } else {
                    bindingDistrictAdapter(city.id, mapDictrict[city.id]!!)
                }

                val tvCity =
                    if (typeAddress == 0) reservation_tv_permanent_city else reservation_tv_contact_city
                tvCity.text = city.city_Name
                val colorDark = Util.getColor(this@ReservationActivity, R.color.color_black)
                tvCity.setTextColor(colorDark)

                val boldFont = Font.boldTypeface(this@ReservationActivity)
                convertView.dialog_address_tv_city.typeface = boldFont

                convertView.dialog_address_tv_city.text = city.city_Name
                convertView.dialog_address_btn_back.visibility = View.VISIBLE
                convertView.dialog_address_btn_close.visibility = View.GONE
                convertView.dialog_address_tv_title.text =
                    getContext().getString(R.string.select_district)
                convertView.dialog_address_recycleView_city.visibility = View.GONE
                convertView.dialog_address_district.visibility = View.VISIBLE
                convertView.dialog_address_tv_all_district.visibility = View.GONE

                convertView.dialog_address_item_all_district.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }

        districtAdapter.listener = object : DistrictItemHandler {
            override fun onClicked(district: District) {
                if (typeAddress == 0) {
                    currentPermantDistrict = district
                } else {
                    currentContactDistrict = district
                }

                val currentCity = if (typeAddress == 0) currentPermantCity else currentContactCity

                val tvCity =
                    if (typeAddress == 0) reservation_tv_permanent_city else reservation_tv_contact_city
                tvCity.text = currentCity!!.city_Name

                val tvDistrict =
                    if (typeAddress == 0) reservation_tv_permanent_district else reservation_tv_contact_district
                val districtName = district.district_Name
                tvDistrict.text = districtName

                val colorDark = Util.getColor(this@ReservationActivity, R.color.color_black)
                tvCity.setTextColor(colorDark)
                tvDistrict.setTextColor(colorDark)
                alertDialog.dismiss()
            }
        }

        convertView.dialog_address_btn_back.setOnClickListener {
            convertView.dialog_address_btn_back.visibility = View.GONE
            convertView.dialog_address_btn_close.visibility = View.VISIBLE
            convertView.dialog_address_tv_title.text = getContext().getString(R.string.select_city)
            convertView.dialog_address_recycleView_city.visibility = View.VISIBLE
            convertView.dialog_address_district.visibility = View.GONE
        }

        convertView.dialog_address_btn_close.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setView(convertView)
        alertDialog.show()
    }

    private fun showSelectDistrict() {
        val currentCity = if (typeAddress == 0) currentPermantCity else currentContactCity
        if (currentCity == null) {
            DialogUtil.showErrorDialog(
                this, getString(R.string.error_not_select_city_title),
                getString(R.string.error_not_select_city_message), getString(R.string.ok), null
            )
        } else {
            if (mapDictrict.containsKey(currentCity.id) && currentCity.id != -1) {
                districtAdapter.setDistrictsList(mapDictrict[currentCity.id]!!)
            } else {
                presenter.loadDistrict(currentCity.id)
            }

            val viewContent =
                if (typeAddress == 0) reservation_view_permanent else reservation_view_contact
            viewContent.setBackgroundColor(Util.getColor(this@ReservationActivity, R.color.white))

            val alertDialog = AlertDialog.Builder(getContext()).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.dialog_address, null) as View
            convertView.dialog_address_tv_title.text =
                getContext().getText(R.string.select_district)
            val districtLayoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(getContext())
            convertView.dialog_address_recycleView_district.layoutManager = districtLayoutManager
            convertView.dialog_address_recycleView_district.adapter = districtAdapter
            val boldFont = Font.boldTypeface(this)
            convertView.dialog_address_tv_city.typeface = boldFont
            convertView.dialog_address_tv_city.text = currentCity.city_Name
            convertView.dialog_address_tv_title.text =
                getContext().getString(R.string.select_district)
            convertView.dialog_address_recycleView_city.visibility = View.GONE
            convertView.dialog_address_tv_all_district.visibility = View.GONE
            convertView.dialog_address_district.visibility = View.VISIBLE
            districtAdapter.listener = object : DistrictItemHandler {
                override fun onClicked(district: District) {
                    if (typeAddress == 0) {
                        currentPermantDistrict = district
                    } else {
                        currentContactDistrict = district
                    }
                    val districtName = district.district_Name
                    val tvDistrict =
                        if (typeAddress == 0) reservation_tv_permanent_district else reservation_tv_contact_district
                    tvDistrict.text = districtName
                    val colorDark = Util.getColor(this@ReservationActivity, R.color.color_black)
                    tvDistrict.setTextColor(colorDark)
                    alertDialog.dismiss()
                }
            }
            convertView.dialog_address_btn_close.setOnClickListener {
                alertDialog.dismiss()
            }

            convertView.dialog_address_item_all_district.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.setView(convertView)
            alertDialog.show()
        }
    }

    private fun initDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = SpinnerDatePickerDialogBuilder().context(this)
            .spinnerTheme(R.style.NumberPickerStyle)
            .callback(this)
            .showTitle(true)
            .showDaySpinner(true)
            .defaultDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            .build()

        val arrayView = arrayOf(reservation_view_birthday, reservation_view_issued_date)
        arrayOf(reservation_tv_birthday, reservation_tv_issued_date).forEachIndexed { index, view ->
            view.setOnClickListener {
                if (isPreviewDeal) {
                    return@setOnClickListener
                }
                datePicker.show()
                currentDateText = view as TextView
                arrayView[index].setBackgroundColor(
                    Util.getColor(
                        this@ReservationActivity,
                        R.color.white
                    )
                )
            }
        }
    }

    private fun initPaymentMethod() {
        val arrayHeader =
            arrayOf(reservation_header_payment_banking, reservation_header_payment_cash)
        val arrayCheckbox = arrayOf(reservation_cb_payment_banking, reservation_cb_payment_cash)
        val arrayContent = arrayOf(reservation_view_payment_banking, reservation_view_payment_cash)
        arrayHeader.forEachIndexed { index, view ->
            view.setOnClickListener {
                if (isEditDeal || isPreviewDeal) {
                    return@setOnClickListener
                }
                reservation_tv_payment.setTextColor(Util.getColor(this, R.color.color_black))
                val isEnable = arrayContent[index].visibility == View.GONE
                arrayCheckbox[index].setImageResource(if (isEnable) R.drawable.check else R.drawable.uncheck)
                arrayContent[index].visibility = if (isEnable) View.VISIBLE else View.GONE

            }
        }

        val param = reservation_tv_cash_invoice.layoutParams
        val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(this, 50)) / 3
        param.height = heightView
        reservation_tv_cash_invoice.layoutParams = param

        val paramImg = reservation_img_cash_invoice.layoutParams
        paramImg.height = heightView
        reservation_img_cash_invoice.layoutParams = paramImg

        reservation_tv_cash_invoice.setOnClickListener {
            if (isPreviewDeal) {
                return@setOnClickListener
            }
            currentType = ReservationImageType.CashInvoice
            showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)
        }

        reservation_img_cash_invoice.setOnClickListener {
            if ((isEditDeal && numOfUploaded > 0) || isPreviewDeal) {
                return@setOnClickListener
            }
            currentType = ReservationImageType.CashInvoice
            showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constant.UPLOAD_IMAGE_REQUEST -> {
                val bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data)
                    ?: return
                val arrayImages = arrayOf(
                    reservation_img_cash_invoice,
                    reservation_img_cmnd,
                    reservation_img_back_cmnd,
                    reservation_img_trang_bia,
                    reservation_img_trang_chu_ho,
                    reservation_img_trang_khach_hang
                )
                val arrayUploadImage = arrayOf(
                    reservation_tv_cash_invoice,
                    reservation_tv_cmnd,
                    reservation_tv_back_cmnd,
                    reservation_tv_trang_bia,
                    reservation_tv_trang_chu_ho,
                    reservation_tv_trang_khach_hang
                )
                currentType?.let {
                    val indexArray = it.value - 2
                    arrayImages[indexArray].setImageBitmap(bitmap)
                    arrayImages[indexArray].visibility = View.VISIBLE
                    arrayUploadImage[indexArray].visibility = View.GONE
                    reservation_progress.visibility = View.VISIBLE
                    presenter.upload(bitmap, it)
                }
            }
            Constant.UPLOAD_IMAGE_INVOICE_REQUEST -> {
                val bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data)
                    ?: return

                val arrayTvBankInvoice = arrayOf(
                    reservation_tv_payment_banking01,
                    reservation_tv_payment_banking02,
                    reservation_tv_payment_banking03,
                    reservation_tv_payment_banking04,
                    reservation_tv_payment_banking05
                )
                val arrayImagesBankInvoice = arrayOf(
                    reservation_img_payment_banking01,
                    reservation_img_payment_banking02,
                    reservation_img_payment_banking03,
                    reservation_img_payment_banking04,
                    reservation_img_payment_banking05
                )
                arrayImagesBankInvoice[indexUploadBankInvoice].setImageBitmap(bitmap)
                arrayImagesBankInvoice[indexUploadBankInvoice].visibility = View.VISIBLE
                arrayTvBankInvoice[indexUploadBankInvoice].visibility = View.GONE
                reservation_progress.visibility = View.VISIBLE
                presenter.upload(bitmap, ReservationImageType.BankInvoice)
            }
            Constant.RESERVATION_PREVIEW_REQUEST -> data?.let { it ->
                val dealResult = it.getSerializableExtra(BUNDLE_KEY_DEAL) as Deal
                setResultPage(dealResult)
            }
        }
    }

    private fun initCollapsible() {
        val arrayInput = arrayOf(reservation_et_range_info, reservation_et_other_info)
        val arrayHeader = arrayOf(
            reservation_header_range_info,
            reservation_header_other_info
        )
        val arrayImg = arrayOf(reservation_img_range_info, reservation_img_other_info)

        arrayHeader.forEachIndexed { index, view ->
            view.setOnClickListener {
                val content = arrayInput[index]
                val img = arrayImg[index]
                img.setImageResource(if (content.visibility == View.GONE) R.drawable.arrow_down else R.drawable.icon_segment_next)
                content.visibility =
                    if (content.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month = monthOfYear + 1
        val date = dateFormat.parse("$dayOfMonth/$month/$year")
        currentDateText?.text = dateFormat.format(date)
    }

    override fun uploadImageCompleted(url: String, type: ReservationImageType) {
        reservation_progress.visibility = View.GONE
        if (type == ReservationImageType.BankInvoice) {
            arrayBankInvoiceLink.add(url)
            return
        }
        mapImages[type.value] = url
    }

    override fun uploadImageFailed(type: ReservationImageType) {
        reservation_progress.visibility = View.GONE
        if (type == ReservationImageType.BankInvoice) {
            val arrayTvBankInvoice = arrayOf(
                reservation_tv_payment_banking01,
                reservation_tv_payment_banking02,
                reservation_tv_payment_banking03,
                reservation_tv_payment_banking04,
                reservation_tv_payment_banking05
            )
            val arrayImagesBankInvoice = arrayOf(
                reservation_img_payment_banking01,
                reservation_img_payment_banking02,
                reservation_img_payment_banking03,
                reservation_img_payment_banking04,
                reservation_img_payment_banking05
            )
            arrayImagesBankInvoice[indexUploadBankInvoice].visibility = View.GONE
            arrayTvBankInvoice[indexUploadBankInvoice].visibility = View.VISIBLE

            DialogUtil.showErrorDialog(
                this, getString(R.string.error_upload_image_failed_title),
                getString(R.string.error_upload_image_failed_desc), getString(R.string.ok), null
            )
            return
        }

        val arrayImages = arrayOf(
            reservation_img_cash_invoice,
            reservation_img_cmnd,
            reservation_img_back_cmnd,
            reservation_img_trang_bia,
            reservation_img_trang_chu_ho,
            reservation_img_trang_khach_hang
        )
        val arrayUploadImage = arrayOf(
            reservation_tv_cash_invoice,
            reservation_tv_cmnd, reservation_tv_back_cmnd,
            reservation_tv_trang_bia, reservation_tv_trang_chu_ho, reservation_tv_trang_khach_hang
        )

        arrayImages[type.value - 2].visibility = View.GONE
        arrayUploadImage[type.value - 2].visibility = View.VISIBLE

        DialogUtil.showErrorDialog(
            this, getString(R.string.error_upload_image_failed_title),
            getString(R.string.error_upload_image_failed_desc), getString(R.string.ok), null
        )
    }

    override fun newReservationCompleted(deal: Deal) {
        reservation_progress.visibility = View.GONE
        setResultPage(deal)
        if (isQuickDeal) {
            startConfirmQuickDeal(this, deal)
        } else {
            reviewDeal(deal)
        }
    }

    private fun setResultPage(deal: Deal) {
        val intent = Intent(this, ReservationActivity::class.java)
        intent.putExtra(BUNDLE_KEY_DEAL, deal)
        setResult(Constant.RESERVATION_REQUEST, intent)
        finish()
    }

    override fun newReservationFailed(message: String?) {
        reservation_progress.visibility = View.GONE
        val error =
            if (!message.isNullOrEmpty()) message else getString(R.string.error_new_reservation_failed_desc)
        DialogUtil.showErrorDialog(
            this, getString(R.string.error_new_reservation_failed_title),
            error, getString(R.string.ok), null
        )
    }

    override fun updateReservationCompleted(deal: Deal) {
        reservation_progress.visibility = View.GONE
        setResultPage(deal)
    }

    override fun updateReservationFailed(message: String?) {
        reservation_progress.visibility = View.GONE
        val error =
            if (!message.isNullOrEmpty()) message else getString(R.string.error_update_reservation_failed_desc)
        DialogUtil.showErrorDialog(
            this, getString(R.string.error_update_reservation_failed_title),
            error, getString(R.string.ok), null
        )
    }

    override fun bindingCities(citiesList: List<City>) {
        cities.clear()
        cities.addAll(citiesList)

        currentContactCity?.let {
            it.id = findCityIdByName(it.city_Name)
        }
        currentPermantCity?.let {
            it.id = findCityIdByName(it.city_Name)
        }

        cityAdapter.setCitiesList(citiesList)
    }

    override fun bindingDistrict(id: Int, districts: List<District>) {
        mapDictrict.put(id, districts)
        bindingDistrictAdapter(id, districts)
    }

    private fun bindingDistrictAdapter(id: Int, districts: List<District>) {
        val currentCity = if (typeAddress == 0) currentPermantCity else currentContactCity
        currentCity?.let {
            if (id == it.id) {
                districtAdapter.setDistrictsList(districts)
            }
        }
    }
}
