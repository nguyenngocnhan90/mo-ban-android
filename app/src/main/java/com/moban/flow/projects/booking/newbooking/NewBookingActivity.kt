package com.moban.flow.projects.booking.newbooking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.moban.R
import com.moban.constant.Constant
import com.moban.extension.isValidPhoneNumber
import com.moban.model.data.booking.BookingParam
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Project
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_new_booking.*
import kotlinx.android.synthetic.main.nav.view.*
import java.text.SimpleDateFormat
import java.util.*

class NewBookingActivity : BaseMvpActivity<INewBookingView, INewBookingPresenter>(), INewBookingView, DatePickerDialog.OnDateSetListener {
    override var presenter: INewBookingPresenter = NewBookingPresenterIml()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private lateinit var project: Project
    private var currentDateText: TextView? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_new_booking
    }

    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(activity: Activity, project: Project) {
            val bundle = Bundle()

            val intent = Intent(activity, NewBookingActivity::class.java)
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)

            activity.startActivityForResult(intent, Constant.NEW_BOOKING_REQUEST)

        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        new_booking_nav.nav_imgBack.visibility = View.INVISIBLE
        new_booking_nav.nav_imgClose.visibility = View.VISIBLE
        new_booking_nav.nav_imgClose.setOnClickListener {
            finish()
        }

        new_booking_nav.nav_tvTitle.text = getString(R.string.new_booking)

        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            return
        }

        project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        initDatePicker()

        new_booking_btn_book_now.setOnClickListener {
            bookingNow()
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

        arrayOf(new_booking_customer_birthday).forEachIndexed { index, view ->
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

    private fun bookingNow() {
        val name = new_booking_customer_name.text.toString()
        val phone = new_booking_customer_phone.text.toString()
        val cmnd = new_booking_customer_cmnd.text.toString()
        val appartment = new_booking_flat_id.text.toString()

        if (name.isBlank()) {
            DialogUtil.showErrorDialog(this, "Thông tin khách không hợp lệ",
                    "Vui lòng nhập Họ và Tên khách", getString(R.string.ok), null)
            return
        }

        if (!phone.isValidPhoneNumber()) {
            DialogUtil.showErrorDialog(this, "Số điện thoại không hợp lệ",
                    "Vui lòng kiểm tra lại.\n(Số điện thoại từ 10 đến 13 chữ số)", getString(R.string.ok), null)
            return
        }

        if (cmnd.isBlank()) {
            DialogUtil.showErrorDialog(this, "Bạn chưa nhập số CMND của khách",
                    "Vui lòng nhập Số CMND của khách", getString(R.string.ok), null)
            return
        }

        val param = BookingParam()
        param.customer_name = name
        param.customer_phone = phone
        param.customer_cmnd = cmnd

        currentDateText?.let {
            param.customer_birthday = it.text.toString()
        }

        param.flat_code = appartment
        param.product_id = project.id

        new_booking_progressbar.visibility = View.VISIBLE
        presenter.book(param)
    }

    override fun bookingCompleted(book: ProjectBooking, message: String) {
        new_booking_progressbar.visibility = View.GONE
        DialogUtil.showInfoDialog(this, "Tạo book căn thành công",
                message, getString(R.string.ok), View.OnClickListener {
            newBookingCompleted()
        })
    }

    private fun newBookingCompleted() {
        setResult(Constant.NEW_BOOKING_REQUEST)
        finish()
    }

    override fun bookingFailed(error: String?) {
        new_booking_progressbar.visibility = View.GONE
        val messgae = error ?: "Vui lòng thử lại sau."
        DialogUtil.showErrorDialog(this, "Tạo book căn thất bại",
                messgae, getString(R.string.ok), null)
    }

}
