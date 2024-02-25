package com.moban.flow.verifyphone

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.flow.main.MainActivity
import com.moban.flow.signin.SignInActivity
import com.moban.helper.LocalStorage
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import kotlinx.android.synthetic.main.activity_verify_phone.*
import kotlinx.android.synthetic.main.nav.view.*

class VerifyPhoneActivity : BaseMvpActivity<IVerifyPhoneView, IVerifyPhonePresenter>(), IVerifyPhoneView {
    override var presenter: IVerifyPhonePresenter = VerifyPhonePresenterIml()
    private var requestTime = 0
    private var user: User? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_verify_phone
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VerifyPhoneActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        verify_phone_nav.nav_tvTitle.text = getString(R.string.register_title)
        verify_phone_nav.nav_imgBack.setOnClickListener {
            SignInActivity.start(this@VerifyPhoneActivity)
        }
        setGAScreenName("Verify phone Account", GACategory.ACCOUNT)

        user = LocalStorage.user()
        user?.let {
            verify_phone_tv_phone.text = it.phone
            verify_phone_progressbar.visibility = View.VISIBLE
            presenter.getOTP(it)
        }

        verify_phone_btn_confirm.setOnClickListener {
            comfirmPhone()
        }
    }

    private fun comfirmPhone() {
        val otp = verify_phone_ed_otp.text.toString()

        if (otp.isEmpty()) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_otp_empty),
                    getString(R.string.error_register_otp_empty_desc),
                    getString(R.string.ok), null)
            return
        }

        //verify_phone_progressbar.visibility = View.VISIBLE
        //TODO: Call api to comfirm
    }

    override fun getOTPCompleted() {
        verify_phone_progressbar.visibility = View.GONE
    }

    override fun showErrorGetOTP() {
        verify_phone_progressbar.visibility = View.GONE
        val error = "Lấy mã xác nhận không thành công. Vui lòng kiểm tra và thử lại."
        val title = "Lấy mã xác nhận không được"
        DialogUtil.showErrorDialog(this, title, error, getString(R.string.ok), null)
    }

    override fun verifyPhoneCompleted() {
        verify_phone_progressbar.visibility = View.GONE
        MainActivity.start(this)
    }

    override fun verifyPhoneFailed(error: String?) {
        val desc = if (error.isNullOrBlank()) "Vui lòng kiểm tra và thử lại." else
            error
        val title = "Gửi yêu cầu xác nhận không thành công"
        var dialog: Dialog? = null
        dialog = DialogUtil.showConfirmDialog(this, true, title, desc, getString(R.string.ok),
                getString(R.string.retry), null, View.OnClickListener {
            dialog?.dismiss()
            resendOTP()
        })
    }

    private fun resendOTP() {
        if (requestTime < 3) {
            user?.let {
                requestTime++
                verify_phone_progressbar.visibility = View.VISIBLE
                presenter.getOTP(it)
            }
        }
    }
}
