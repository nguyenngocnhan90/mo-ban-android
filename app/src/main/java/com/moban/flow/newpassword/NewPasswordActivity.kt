package com.moban.flow.newpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.enum.GACategory
import com.moban.flow.signin.SignInActivity
import com.moban.model.param.user.RecoverPasswordParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import kotlinx.android.synthetic.main.activity_new_password.*
import kotlinx.android.synthetic.main.nav.view.*

class NewPasswordActivity :  BaseMvpActivity<INewPasswordView, INewPasswordPresenter>(), INewPasswordView {
    override var presenter: INewPasswordPresenter = NewPasswordPresenterIml()
    private lateinit var userName: String
    override fun getLayoutId(): Int {
        return R.layout.activity_new_password
    }

    companion object {
        private const val BUNDLE_KEY_USERNAME = "BUNDLE_KEY_USERNAME"

        fun start(context: Context, userName: String) {
            val intent = Intent(context, NewPasswordActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_USERNAME, userName)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("New Password", GACategory.ACCOUNT)
        new_pass_tb_toolbar.nav_tvTitle.text = getString(R.string.new_password_title)
        new_pass_tb_toolbar.nav_imgBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_USERNAME)) {
            userName = bundle?.getSerializable(BUNDLE_KEY_USERNAME) as String
        }

        new_pass_btn_confirm_password.setOnClickListener {
            handleRecoverPassword()
        }
    }

    private fun handleRecoverPassword() {
        val otp = new_pass_ed_otp.text.toString()
        val password = new_pass_ed_password.text.toString()
        val passwordConfirm = new_pass_ed_password_confirm.text.toString()

        if (otp.isEmpty()) {
            onRecoverPasswordFailed(getString(R.string.otp_is_empty_error), getString(R.string.otp_is_empty_message))
            return
        }

        if (password.isEmpty() || passwordConfirm.isEmpty()) {
            onRecoverPasswordFailed(getString(R.string.new_password_is_empty_error), getString(R.string.new_password_is_empty_message))
            return
        }

        if (password != passwordConfirm) {
            onRecoverPasswordFailed(getString(R.string.password_is_not_match_error), getString(R.string.password_is_not_match_message))
            return
        }

        val param = RecoverPasswordParam()
        param.otp = otp
        param.new_password = password
        param.username = userName

        new_pass_progressbar.visibility = View.VISIBLE

        presenter.recoverPassword(param)
    }

    override fun onRecoverPasswordFailed(title: String?, error: String) {
        new_pass_progressbar.visibility = View.GONE
        val titleDialog = if (title.isNullOrEmpty()) getString(R.string.can_not_change_password_error) else title!!
        DialogUtil.showErrorDialog(this, titleDialog , error,
                getString(R.string.ok), null)
    }

    override fun onRecoverPasswordCompleted() {
        new_pass_progressbar.visibility = View.GONE
        SignInActivity.start(this, userName)
    }
}
