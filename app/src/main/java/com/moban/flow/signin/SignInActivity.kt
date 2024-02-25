package com.moban.flow.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.flow.account.changepassword.ChangePasswordActivity
import com.moban.flow.account.register.RegisterActivity
import com.moban.flow.forgotpassword.ForgotPasswordActivity
import com.moban.flow.main.MainActivity
import com.moban.flow.verifyphone.VerifyPhoneActivity
import com.moban.helper.LocalStorage
import com.moban.mvp.BaseMvpActivity
import com.moban.util.AppUtil
import com.moban.util.DialogUtil
import com.moban.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : BaseMvpActivity<ISignInView, ISignInPresenter>(), ISignInView {

    companion object {
        private const val BUNDLE_KEY_USERNAME = "BUNDLE_KEY_USERNAME"
        private const val BUNDLE_KEY_FROM_ACTIVITY = "BUNDLE_KEY_FROM_ACTIVITY"

        fun start(context: Context, userName: String? = null) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            userName?.let {
                val bundle = Bundle()
                bundle.putSerializable(BUNDLE_KEY_USERNAME, it)
                intent.putExtras(bundle)
            }

            context.startActivity(intent)
        }

        fun start(context: Context, fromActivity: Activity) {
            val intent = Intent(context, SignInActivity::class.java)

            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_FROM_ACTIVITY, fromActivity.localClassName)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }

    }

    override var presenter: ISignInPresenter = SignInPresenterIml()
    private var fromActivity: String? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_sign_in
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_USERNAME)) {
            val userName = bundle?.getSerializable(BUNDLE_KEY_USERNAME) as String
            sign_in_edtUsername.setText(userName)
            sign_in_edtPassword.requestFocus()
            sign_in_edtUsername.postDelayed({
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.showSoftInput(sign_in_edtUsername, 0)
            }, 200)
        }

        if (intent.hasExtra(BUNDLE_KEY_FROM_ACTIVITY)) {
            fromActivity = bundle?.getString(BUNDLE_KEY_FROM_ACTIVITY)
        }

        if (fromActivity == null) {
            cleanPersonalData()
        }

        sign_in_button_anonymous.setOnClickListener {
            if (fromActivity != null) {
                finish()
            }
            else {
                KeyboardUtil.forceHideKeyboard(this)
                sign_in_progressbar.visibility = View.VISIBLE
                presenter.loginAnonymous()
            }
        }

        sign_in_btnLogin.setOnClickListener {
            KeyboardUtil.forceHideKeyboard(this)
            sign_in_progressbar.visibility = View.VISIBLE
            presenter.handleSignIn()
        }

        sign_in_tv_contact_support.setOnClickListener {
            openFacebookMessenger()
        }

        sign_in_tv_register.setOnClickListener {
            RegisterActivity.start(this@SignInActivity)
        }

        sign_in_tv_forgot_password.setOnClickListener {
            startForgotPassword()
        }

        setGAScreenName("Sign In", GACategory.STARTUP)
    }

    private fun startForgotPassword() {
        ForgotPasswordActivity.start(this)
    }

    private fun cleanPersonalData() {
        LocalStorage.saveToken(null)
        LocalStorage.saveUser(null)
    }

    override fun getInputUsername(): String {
        return sign_in_edtUsername.text.toString()
    }

    override fun getInputPassword(): String {
        return sign_in_edtPassword.text.toString()
    }

    override fun showInputNeededError() {
        sign_in_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this,
                getString(R.string.signIn_input_error),
                getString(R.string.signIn_input_error_msg),
                getString(R.string.ok), null)
    }

    override fun showLoginError(title: String, content: String) {
        sign_in_progressbar.visibility = View.GONE
        DialogUtil.showErrorDialog(this, title, content, getString(R.string.ok), null)
    }

    override fun startMainScreen() {
        sign_in_progressbar.visibility = View.GONE
        MainActivity.start(this)
    }

    override fun openFacebookMessenger() {
        val fanpageId = Constant.FACEBOOK_FANPAGE_ID
        AppUtil.openFacebookMessenger(this, fanpageId)
    }

    override fun startVerifyPhone() {
        VerifyPhoneActivity.start(this)
    }

    override fun changePassword() {
        ChangePasswordActivity.start(this, true)
        finish()
    }
}
