package com.moban.flow.reservation.update

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.mvc.imagepicker.ImagePicker
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.ApproveStatus
import com.moban.enum.DealStatus
import com.moban.enum.DocumentType
import com.moban.model.data.deal.Deal
import com.moban.model.param.DocumentParam
import com.moban.model.param.NewReservationParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_update_reservation.*
import kotlinx.android.synthetic.main.nav.view.*

class UpdateReservationActivity : BaseMvpActivity<IUpdateReservationView, IUpdateReservationPresenter>(), IUpdateReservationView {

    override var presenter: IUpdateReservationPresenter = UpdateReservationPresenterIml()

    private var deal: Deal? = null
    private var nextDealStatus: DealStatus = DealStatus.DEPOSITED

    var title: String = ""
    var documentTypeName: String = ""

    private var indexUploadInvoice = 0
    private val arrayImageLinks: MutableList<String> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.activity_update_reservation
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_DEAL)) {
            deal = bundle?.getSerializable(BUNDLE_KEY_DEAL) as? Deal
        }
        if (intent.hasExtra(BUNDLE_NEXT_STATUS)) {
            nextDealStatus = DealStatus.from(bundle?.getInt(BUNDLE_NEXT_STATUS) ?: 0)
        }

        val titleId = when (nextDealStatus) {
            DealStatus.DEPOSITED -> R.string.deal_change_to_coc
            DealStatus.CONTRACTED -> R.string.deal_change_to_contract
            else -> R.string.deal_refund
        }
        title = getString(titleId)
        update_reservation_nav.nav_tvTitle.text = title

        val documentNameId = when (nextDealStatus) {
            DealStatus.DEPOSITED -> R.string.deal_change_to_coc_document_name
            DealStatus.CONTRACTED -> R.string.related_document
            else -> R.string.deal_refund
        }
        documentTypeName = getString(documentNameId)
        update_reservation_tv_name_of_images.text = documentTypeName

        fillDealInfo()
        initListImages()
        update_reservation_view_reason.visibility = if (nextDealStatus == DealStatus.CANCELED) View.VISIBLE else View.GONE

        update_reservation_btn_update.setOnClickListener {
            updateDeal()
        }
    }

    private fun initListImages() {
        val arrayTexts = arrayOf(update_reservation_tv_chung_tu_01, update_reservation_tv_chung_tu_02,
                update_reservation_tv_chung_tu_03, update_reservation_tv_chung_tu_04, update_reservation_tv_chung_tu_05)
        val arrayImages = arrayOf(update_reservation_img_chung_tu_01, update_reservation_img_chung_tu_02,
                update_reservation_img_chung_tu_03, update_reservation_img_chung_tu_04, update_reservation_img_chung_tu_05)

        arrayTexts.forEachIndexed { index, view ->
            val param = view.layoutParams
            val heightView = (Device.getScreenWidth() - BitmapUtil.convertDpToPx(this, 50)) / 3
            param.height = heightView
            param.width = heightView
            view.layoutParams = param

            val paramImg = arrayImages[index].layoutParams
            paramImg.height = heightView
            paramImg.width = heightView
            arrayImages[index].layoutParams = paramImg

            view.setOnClickListener {
                indexUploadInvoice = index
                showImagePicker(Constant.UPLOAD_IMAGE_REQUEST)
            }
        }
    }

    private fun fillDealInfo() {
        deal?.let { deal ->
            LHPicasso.loadImage(deal.product_Image, update_reservation_img_logo)
            update_reservation_tv_project_name.text = deal.product_Name

            val title = deal.customer_Name_Exact
            update_reservation_tv_name.text = title
            update_reservation_tv_price.text = deal.deal_Price_String
            update_reservation_tv_date.text = deal.deal_Date_String
            update_reservation_tv_order_status.text = deal.deal_Status_String
            update_reservation_tv_order_status.setTextColor(Color.parseColor(deal.deal_Status_Color))

            val index = deal.booking_Index
            val isInvalid = index == 0 || deal.approve_Status != ApproveStatus.Approved.value
            update_reservation_tv_order_num.text = if (isInvalid) "" else index.toString()
            update_reservation_view_order_num.visibility = if (isInvalid) View.GONE else View.VISIBLE
        }
    }

    private fun updateDeal() {
        if (!NetworkUtil.hasConnection(this)) {
            return
        }

        val param = NewReservationParam()
        param.id = deal?.id ?: 0
        param.deal_status = nextDealStatus.value
        param.reason = update_reservation_et_reason.text.toString()
        val documentType = if (nextDealStatus == DealStatus.DEPOSITED) DocumentType.phieuCoc else DocumentType.hopDong_MuaBan

        param.upload_files = arrayImageLinks.map {
            val document = DocumentParam()
            document.document_type_id = documentType.value
            document.link = it
            document
        }

        update_reservation_progress.visibility = View.VISIBLE
        presenter.update(param)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.UPLOAD_IMAGE_REQUEST) {
            val bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data)
                    ?: return

            val arrayTexts = arrayOf(update_reservation_tv_chung_tu_01, update_reservation_tv_chung_tu_02,
                    update_reservation_tv_chung_tu_03, update_reservation_tv_chung_tu_04, update_reservation_tv_chung_tu_05)
            val arrayImages = arrayOf(update_reservation_img_chung_tu_01, update_reservation_img_chung_tu_02,
                    update_reservation_img_chung_tu_03, update_reservation_img_chung_tu_04, update_reservation_img_chung_tu_05)
            arrayImages[indexUploadInvoice].setImageBitmap(bitmap)
            arrayImages[indexUploadInvoice].visibility = View.VISIBLE
            arrayTexts[indexUploadInvoice].visibility = View.GONE

            update_reservation_progress.visibility = View.VISIBLE
            presenter.upload(bitmap)
        }
    }

    private fun showImagePicker(requestCode: Int) {
        if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionWriteExternalStorage(this) &&
                Permission.checkPermissionCamera(this)) {
            BitmapUtil.openPickImage(this, requestCode)
        }
    }

    override fun uploadImageCompleted(url: String) {
        update_reservation_progress.visibility = View.GONE
        arrayImageLinks.add(url)
    }

    override fun uploadImageFailed() {
        update_reservation_progress.visibility = View.GONE

        val arrayTexts = arrayOf(update_reservation_tv_chung_tu_01, update_reservation_tv_chung_tu_02,
                update_reservation_tv_chung_tu_03, update_reservation_tv_chung_tu_04, update_reservation_tv_chung_tu_05)
        val arrayImages = arrayOf(update_reservation_img_chung_tu_01, update_reservation_img_chung_tu_02,
                update_reservation_img_chung_tu_03, update_reservation_img_chung_tu_04, update_reservation_img_chung_tu_05)

        arrayImages[indexUploadInvoice].visibility = View.GONE
        arrayTexts[indexUploadInvoice].visibility = View.VISIBLE

        DialogUtil.showErrorDialog(this, getString(R.string.error_upload_image_failed_title),
                getString(R.string.error_upload_image_failed_desc), getString(R.string.ok), null)
    }

    override fun updateReservationCompleted(deal: Deal) {
        val intent = Intent(this, UpdateReservationActivity::class.java)
        intent.putExtra(BUNDLE_KEY_DEAL, deal)
        setResult(Constant.RESERVATION_REQUEST, intent)
        finish()
    }

    override fun updateReservationFailed(message: String?) {
        update_reservation_progress.visibility = View.GONE
        val error = if (!message.isNullOrEmpty()) message else getString(R.string.error_update_reservation_failed_desc)
        DialogUtil.showErrorDialog(this, getString(R.string.error_update_reservation_failed_title),
                error, getString(R.string.ok), null)
    }

    companion object {
        private const val BUNDLE_KEY_DEAL = "BUNDLE_KEY_DEAL"
        private const val BUNDLE_NEXT_STATUS = "BUNDLE_NEXT_STATUS"

        fun start(activity: Activity, deal: Deal, nextStatus: DealStatus) {
            val intent = Intent(activity, UpdateReservationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_DEAL, deal)
            bundle.putInt(BUNDLE_NEXT_STATUS, nextStatus.value)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.RESERVATION_REQUEST)
        }
    }
}