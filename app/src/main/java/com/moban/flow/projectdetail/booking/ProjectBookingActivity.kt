package com.moban.flow.projectdetail.booking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.FlatStatus
import com.moban.enum.GACategory
import com.moban.extension.isValidPhoneNumber
import com.moban.model.data.booking.BookingParam
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Apartment
import com.moban.model.data.project.Block
import com.moban.model.data.project.Project
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.KeyboardUtil
import com.moban.util.Util
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_project_booking.*
import kotlinx.android.synthetic.main.item_booking_detail_generate.*
import kotlinx.android.synthetic.main.nav.view.*
import java.text.SimpleDateFormat
import java.util.*

class ProjectBookingActivity : BaseMvpActivity<IProjectBookingView, IProjectBookingPresenter>(), IProjectBookingView, DatePickerDialog.OnDateSetListener {

    private var project: Project? = null
    private var block: Block? = null
    private var apartment: Apartment? = null
    override var presenter: IProjectBookingPresenter = ProjectBookingPresenterIml()
    private var currentDateText: TextView? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    companion object {
        const val BUNDLE_KEY_CODE = "BUNDLE_KEY_CODE"
        const val BUNDLE_KEY_BLOCK = "BUNDLE_KEY_BLOCK"
        const val BUNDLE_KEY_APARTMENT = "BUNDLE_KEY_APARTMENT"
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(activity: Activity, project: Project, block: Block, apartment: Apartment) {
            val intent = Intent(activity, ProjectBookingActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            bundle.putSerializable(BUNDLE_KEY_BLOCK, block)
            bundle.putSerializable(BUNDLE_KEY_APARTMENT, apartment)

            intent.putExtras(bundle)
            activity.startActivityForResult(intent, Constant.PROJECT_BOOKING_REQUEST)
        }

    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        }

        if (intent.hasExtra(BUNDLE_KEY_BLOCK)) {
            block = bundle?.getSerializable(BUNDLE_KEY_BLOCK) as Block
            block?.let {
                if (it.block_Name.isNotEmpty()) {
                    project_detail_block.text = it.block_Name
                }
            }
        }

        if (intent.hasExtra(BUNDLE_KEY_APARTMENT)) {
            apartment = bundle?.getSerializable(BUNDLE_KEY_APARTMENT) as Apartment
            apartment?.let {
                if (it.flat_Name.isNotEmpty()) {
                    project_detail_apartment.text = it.flat_Name
                    val nameDisplay = getString(R.string.apartment) + " " + it.flat_Name
                    project_booking_nav.nav_tvTitle.text = nameDisplay
                }

                if (it.flat_Area.isNotEmpty()) {
                    project_detail_apartment_size.text = it.flat_Area
                }

                val priceUnit = getString(R.string.vnd)
                val priceWithoutVAT = it.flat_Price_Without_VAT
                val priceWithoutVATString = if (priceWithoutVAT == 0L) "--" else (it.getWithoutVATPriceString() + " " + priceUnit)
                project_detail_apartment_price_without_vat.text = priceWithoutVATString

                val fullPrice = it.getFullPriceString() + " " + priceUnit
                project_detail_apartment_cost.text = fullPrice

                project_booking_btn_book_flat.isEnabled = it.isBookable
                project_booking_book_flat.visibility = if (it.isBookable) View.VISIBLE else View.GONE
                project_booking_btn_book_now.visibility = if (it.isBookable) View.VISIBLE else View.GONE

                val flatStatus = FlatStatus.from(it.flat_Status)
                project_detail_apartment_status_background.background = flatStatus.getBackgroundRound15(this)
                project_detail_apartment_status.text = it.flat_Status_Title.capitalize()
            }
        }

        project_booking_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        project_detail_project_view.visibility = View.GONE

        initDatePicker()
        initBookingBtn()

        apartment?.let {
            presenter.loadApartment(it)
            setGAScreenName("Project Booking Apartment Id $${it.id}", GACategory.ACCOUNT)
        }
    }

    private fun initDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = SpinnerDatePickerDialogBuilder().context(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .callback(this)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .build()

        arrayOf(project_detail_customer_birthday).forEachIndexed { index, view ->
            view.setOnClickListener {
                datePicker.show()
                currentDateText = view as TextView
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month = monthOfYear + 1
        val date = dateFormat.parse("$dayOfMonth/$month/$year")
        currentDateText?.text = dateFormat.format(date)
    }

    private fun initBookingBtn() {
        project_booking_btn_book_now.setOnClickListener {
            submitBooking()
        }

        val contentViewArr = arrayOf(project_booking_overview, project_booking_book_flat)
        val btnTabs = arrayOf(project_booking_btn_book_overview, project_booking_btn_book_flat)
        val lineTabs = arrayOf(project_booking_line_book_overview, project_booking_line_book_flat)
        btnTabs.forEachIndexed { idx, button ->
            button.setOnClickListener {
                btnTabs.forEachIndexed { idxSelected, view ->
                    val textColor = if (idxSelected == idx) R.color.white else R.color.color_black_50
                    view.setTextColor(Util.getColor(this@ProjectBookingActivity, textColor))
                }
                lineTabs.forEachIndexed { idxSelected, view ->
                    val visibility = if (idxSelected == idx) View.VISIBLE else View.GONE
                    view.visibility = visibility
                }
                project_booking_scroll_view.scrollBy(0, contentViewArr[idx].y.toInt())
            }
        }

    }

    private fun submitBooking() {
        KeyboardUtil.hideKeyboard(this)

        val customerName = project_detail_customer_name.text.toString()
        val customerPhone = project_detail_customer_phone.text.toString()
        val cmnd = project_detail_customer_cmnd.text.toString()

        if (customerName.isBlank()) {
            DialogUtil.showErrorDialog(this, "Thông tin khách không hợp lệ",
                    "Vui lòng nhập Họ và Tên khách", getString(R.string.ok), null)
            return
        }

        if (!customerPhone.isValidPhoneNumber()) {
            DialogUtil.showErrorDialog(this, "Số điện thoại không hợp lệ",
                    "Vui lòng kiểm tra lại.\n(Số điện thoại từ 10 đến 13 chữ số)", getString(R.string.ok), null)
            return
        }

        if (cmnd.isBlank()) {
            DialogUtil.showErrorDialog(this, "Bạn chưa nhập số CMND của khách",
                    "Vui lòng nhập Số CMND của khách", getString(R.string.ok), null)
            return
        }

        project_booking_progressbar.visibility = View.VISIBLE
        apartment?.let {
            val param = BookingParam()
            param.customer_name = customerName
            param.customer_phone = customerPhone
            param.customer_cmnd = cmnd
            currentDateText?.let {
                param.customer_birthday = it.text.toString()
            }
            param.flat_id = it.id
            presenter.bookingApartment(param)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_project_booking
    }

    override fun loadApartment(apartment: Apartment) {
        this.apartment = apartment
        apartment.let {
            val textViews: Array<TextView> = arrayOf(
                    project_detail_apartment_num_room,
                    project_detail_apartment_size,
                    project_detail_apartment_direction)
            for ((idx, textView) in textViews.withIndex()) {
                textView.text = it.info[idx].value
            }
            project_detail_floor.text = it.floor_Name

            project_detail_view_note.visibility = View.VISIBLE
            project_detail_tv_note.text = it.note
        }
    }

    override fun showBookingApartmentSuccess(projectBooking: ProjectBooking, message: String?) {
        project_booking_progressbar.visibility = View.GONE
        val message = message ?: ""
        DialogUtil.showInfoDialog(this, getString(R.string.booking_completed_title),
                message, getString(R.string.ok), View.OnClickListener {
            newBookingCompleted()
        })
    }

    private fun newBookingCompleted() {
        val intent = Intent()
        intent.putExtra(BUNDLE_KEY_CODE, 1)
        setResult(Constant.PROJECT_BOOKING_REQUEST, intent)
        finish()
    }

    override fun showBookingApartmentFailed(message: String?) {
        project_booking_progressbar.visibility = View.GONE
        val messgae = message ?: "Vui lòng thử lại sau."
        DialogUtil.showErrorDialog(this,
                getString(R.string.booking_failed_title),
                messgae,
                getString(R.string.ok), null)
    }

}
