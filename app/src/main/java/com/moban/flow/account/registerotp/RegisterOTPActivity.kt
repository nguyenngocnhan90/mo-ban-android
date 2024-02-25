package com.moban.flow.account.registerotp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.flow.main.MainActivity
import com.moban.model.param.user.RegisterParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_register_otp.*
import kotlinx.android.synthetic.main.nav.view.*

class RegisterOTPActivity : BaseMvpActivity<IRegisterOTPView, IRegisterOTPPresenter>(), IRegisterOTPView {
    override var presenter: IRegisterOTPPresenter = RegisterOTPPresenterIml()
    private lateinit var param: RegisterParam
    companion object {
        const val BUNDLE_KEY_REGISTER_PARAM = "BUNDLE_KEY_REGISTER_PARAM"

        fun start(context: Context, param: RegisterParam) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_REGISTER_PARAM, param)

            val intent = Intent(context, RegisterOTPActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register_otp
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        register_otp_nav.nav_tvTitle.text = getString(R.string.register_title)
        register_otp_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        if (!intent.hasExtra(BUNDLE_KEY_REGISTER_PARAM)) {
            return
        }

        param = bundle?.getSerializable(BUNDLE_KEY_REGISTER_PARAM) as RegisterParam
        setGAScreenName("Register OTP Account", GACategory.ACCOUNT)

        register_otp_btn_register.setOnClickListener {
            confirmRegister()
        }

        val otp = param.otp
        register_otp_ed_otp.setText(otp)
        if (otp.isEmpty()) {
            register_otp_ed_otp.requestFocus()
        }
        else {
            register_otp_ed_password.requestFocus()
        }

        Handler().postDelayed({
            KeyboardUtil.showKeyboard(this)
        }, 500)
    }

    private fun confirmRegister() {
        val otp = register_otp_ed_otp.text.toString()
        val password = register_otp_ed_password.text.toString()
        val confirmPassword = register_otp_ed_password_confirm.text.toString()

        if (otp.isEmpty()) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_otp_empty),
                    getString(R.string.error_register_otp_empty_desc),
                    getString(R.string.ok), null)
            return
        }

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_password_empty),
                    getString(R.string.error_register_password_empty_desc),
                    getString(R.string.ok), null)
            return
        }

        if (confirmPassword != password) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_password_not_match),
                    getString(R.string.error_register_password_not_match_desc),
                    getString(R.string.ok), null)
            return
        }

        param.otp = otp
        param.password = password
        register_otp_progressbar.visibility = View.VISIBLE
        presenter.register(param)
    }

    override fun showRegisterFailed(message: String?) {
        register_otp_progressbar.visibility = View.GONE
        val error = if (!message.isNullOrEmpty()) message else getString(R.string.error_register_failed)
        DialogUtil.showErrorDialog(this, "", error, getString(R.string.ok), null)
    }

    override fun showRegisterSuccessful() {
        register_otp_progressbar.visibility = View.GONE
        var dialog: Dialog? = null
        dialog = DialogUtil.showInfoDialog(this, getString(R.string.register_congrats_title), getString(R.string.register_confirmation_success),
                getString(R.string.ok), View.OnClickListener {
            dialog?.dismiss()
            goHome()
        })
    }

    private fun goHome() {
        MainActivity.start(this)
    }
}
