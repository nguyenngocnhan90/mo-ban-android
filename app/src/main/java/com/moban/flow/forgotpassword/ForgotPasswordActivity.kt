package com.moban.flow.forgotpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.flow.newpassword.NewPasswordActivity
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.nav.view.*

class ForgotPasswordActivity : BaseMvpActivity<IForgotPasswordView, IForgotPasswordPresenter>(), IForgotPasswordView {
    override var presenter: IForgotPasswordPresenter = ForgotPasswordPresenterIml()
    private var userName: String = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_forgot_password
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Forgot Password", GACategory.ACCOUNT)
        forgot_pass_nav.nav_tvTitle.text = getString(R.string.forgot_password)
        forgot_pass_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        forgot_pass_btn_reset.setOnClickListener {
            forgot_pass_progressbar.visibility = View.VISIBLE
            userName = forgot_pass_ed_username.text.toString()
            presenter.resetPassword(userName)
        }

        forgot_pass_ed_username.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(text: Editable?) {
                forgot_pass_btn_reset.isEnabled = !text.isNullOrBlank()
                val textColor = if (text.isNullOrEmpty())
                    Util.getColor(this@ForgotPasswordActivity, R.color.color_light_gray) else
                    Util.getColor(this@ForgotPasswordActivity, R.color.color_white)
                forgot_pass_btn_reset.setTextColor(textColor)
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onResetPasswordCompleted() {
        forgot_pass_progressbar.visibility = View.GONE
        NewPasswordActivity.start(this, userName)
    }

    override fun onResetPasswordFailed(error: String) {
        forgot_pass_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this, getString(R.string.cannot_request_error), error,
                getString(R.string.ok), null)
    }
}
