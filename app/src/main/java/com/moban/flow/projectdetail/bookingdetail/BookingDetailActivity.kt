package com.moban.flow.projectdetail.bookingdetail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.CartStatus
import com.moban.enum.GACategory
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Apartment
import com.moban.mvp.BaseMvpActivity
import com.moban.util.BitmapUtil
import com.moban.util.DialogUtil
import com.moban.util.LHPicasso
import com.moban.util.Permission
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_booking_detail.*
import kotlinx.android.synthetic.main.item_booking_detail_generate.*
import kotlinx.android.synthetic.main.nav.view.*


class BookingDetailActivity : BaseMvpActivity<IBookingDetailView, IBookingDetailPresenter>(), IBookingDetailView {

    override var presenter: IBookingDetailPresenter = BookingDetailPresenterIml()
    private var projectBooking: ProjectBooking? = null
    private var id: Int? = null

    companion object {
        const val TYPE_BOOKING_INVOICE = 1
        const val TYPE_INVOICE = 2
        const val BUNDLE_KEY_PROJECT_BOOKING = "BUNDLE_KEY_PROJECT_BOOKING"
        const val BUNDLE_KEY_PROJECT_BOOKING_ID = "BUNDLE_KEY_PROJECT_BOOKING_ID"

        fun start(context: Context, projectBooking: ProjectBooking) {
            val intent = Intent(context, BookingDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT_BOOKING, projectBooking)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun start(context: Context, id: Int) {
            val intent = Intent(context, BookingDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT_BOOKING_ID, id)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PROJECT_BOOKING)) {
            projectBooking = bundle?.getSerializable(BUNDLE_KEY_PROJECT_BOOKING) as ProjectBooking
            projectBooking?.let {
                presenter.loadBookingDetail(it.id)
                project_detail_block.text = it.block_Name
                project_detail_apartment.text = it.flat_Name
                val cartStatus = CartStatus.from(it.cart_Status)
                project_detail_apartment_status.text = it.cart_Status_Title
                cartStatus?.let { status ->
                    project_detail_apartment_status_background.background = status.backgroundColor(this)
                }
                if (!it.isOutOfTime()) {
                    countDown(it.remainingSecond())
                }
                setGAScreenName("Project Booking Detail ${it.id}", GACategory.PROJECT)
            }
        } else if (intent.hasExtra(BUNDLE_KEY_PROJECT_BOOKING_ID)) {
            id = bundle?.getSerializable(BUNDLE_KEY_PROJECT_BOOKING_ID) as Int
            presenter.loadBookingDetail(id!!)
            setGAScreenName("Project Booking Detail $id", GACategory.PROJECT)
        }

        booking_detail_nav.nav_tvTitle.text = getText(R.string.transaction)
        booking_detail_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        booking_detail_btn_booking_cmnd.setOnClickListener {
            if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionWriteExternalStorage(this)) {
                BitmapUtil.openPickImage(this, Constant.BOOKING_DETAIL_BOOKING_INVOICE)
            }
        }

        booking_detail_btn_invoice.setOnClickListener {
            if (Permission.checkPermissionReadExternalStorage(this) && Permission.checkPermissionWriteExternalStorage(this)) {
                BitmapUtil.openPickImage(this, Constant.BOOKING_DETAIL_INVOICE)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_booking_detail
    }

    override fun loadApartmentDetail(projectBooking: ProjectBooking) {
        this.projectBooking = projectBooking
        if (projectBooking.product_Name.isNotEmpty()) {
            project_detail_project.text = projectBooking.product_Name
        }
        projectBooking.flat_Info?.let {
            fillApartmentDetail(it)
        }

        fillBookingInfo()
    }

    private fun fillApartmentDetail(apartment: Apartment) {

        if (apartment.flat_Name.isNotEmpty()) {
            project_detail_apartment.text = apartment.flat_Name
        }

        if (apartment.flat_Area.isNotEmpty()) {
            project_detail_apartment_size.text = apartment.flat_Area
        }

        if (apartment.floor_Name.isNotEmpty()) {
            project_detail_floor.text = apartment.floor_Name
        }

        val priceString = apartment.getFullPriceString() + " " + getString(R.string.vnd)
        project_detail_apartment_cost.text = priceString

        val textViews: Array<TextView> = arrayOf(
                project_detail_apartment_num_room,
                project_detail_apartment_size,
                project_detail_apartment_direction)
        for ((idx, textView) in textViews.withIndex()) {
            textView.text = apartment.info[idx].value
        }
    }

    private fun fillBookingInfo() {
        projectBooking?.let {
            if (it.customer_Name.isNotEmpty()) {
                booking_detail_customer_name.text = it.customer_Name
            }

            if (it.customer_Phone.isNotEmpty()) {
                booking_detail_customer_phone.text = it.customer_Phone
            }

            if (it.customer_CMND.isNotEmpty()) {
                booking_detail_customer_cmmd_passport.text = it.customer_CMND
            }

            if (it.customer_Birthday.isNotEmpty()) {
                booking_detail_customer_birthday.text = it.customer_Birthday
            }

            if (it.customer_Invoice_Booking.isNotEmpty()) {
                LHPicasso.loadImage(it.customer_Invoice_Booking, booking_detail_img_booking_invoice)
            }

            if (it.customer_Invoice.isNotEmpty()) {
                LHPicasso.loadImage(it.customer_Invoice, booking_detail_img_invoice)
            }

            // Editable
            val isEditable = it.isEditable()
            val imageUploadVisibility = if (isEditable) View.VISIBLE else View.GONE
            booking_detail_view_cmnd_image.visibility = imageUploadVisibility
            booking_detail_view_invoice_image.visibility = imageUploadVisibility
        }
    }

    private fun countDown(sec: Int) {
        projectBooking?.let {
            booking_detail_circle_count_down.max = it.timerSecond()
        }

        val countDownTimer = object : CountDownTimer((sec * 1000).toLong(), 1000) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds / 1000
                booking_detail_circle_count_down.progress = seconds.toInt()

                val secString = projectBooking?.remainingTimeString()
                booking_detail_tv_timer.text = secString
            }

            override fun onFinish() {
                project_detail_apartment_status_background.background = getDrawable(R.drawable.background_project_detail_round_red)
                projectBooking?.let { status ->
                    project_detail_apartment_status.text = status.cart_Status_Title
                }

                booking_detail_tv_timer.text = getText(R.string.project_detail_transaction_end_time)
                booking_detail_view_cmnd_image.visibility = View.GONE
                booking_detail_view_invoice_image.visibility = View.GONE
            }
        }
        countDownTimer.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data) ?: return

        if (requestCode == Constant.BOOKING_DETAIL_BOOKING_INVOICE) {
            projectBooking?.let {
                presenter.uploadInvoice(it, bitmap, TYPE_BOOKING_INVOICE)
            }

        } else {
            if (requestCode == Constant.BOOKING_DETAIL_INVOICE) {
                projectBooking?.let {
                    presenter.uploadInvoice(it, bitmap, TYPE_INVOICE)
                }
            }
        }
    }

    override fun showDialogUpdateInvoiceSuccess(bitmap: Bitmap, type: Int, message: String) {
        if (type == TYPE_BOOKING_INVOICE) {
            booking_detail_img_booking_invoice.setImageBitmap(bitmap)
        } else {
            booking_detail_img_invoice.setImageBitmap(bitmap)
        }

        DialogUtil.showInfoDialog(this,
                getString(R.string.congrats),
                message,
                getString(R.string.ok), null)
    }

    override fun showDialogUpdateInvoiceFailed(type: Int) {
        DialogUtil.showErrorDialog(this,
                getString(R.string.failed),
                getString(R.string.update_invoice_booking_error_msg),
                getString(R.string.ok), null)
    }
}
