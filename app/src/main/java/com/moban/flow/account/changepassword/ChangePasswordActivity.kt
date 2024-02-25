package com.moban.flow.account.changepassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.moban.R
import com.moban.enum.GACategory
import com.moban.flow.main.MainActivity
import com.moban.helper.LocalStorage
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.Util
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.nav.view.*


class ChangePasswordActivity : BaseMvpActivity<IChangePasswordView, IChangePasswordPresenter>(), IChangePasswordView {
    override var presenter: IChangePasswordPresenter = ChangePasswordPresenterIml()
    private var isForceChange = false

    companion object {
        const val BUNDLE_KEY_FORCE_PARAM = "BUNDLE_KEY_FORCE_PARAM"

        fun start(context: Context, forceChangePass: Boolean = false) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_FORCE_PARAM, forceChangePass)

            val intent = Intent(context, ChangePasswordActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_FORCE_PARAM)) {
            isForceChange = bundle?.getSerializable(BUNDLE_KEY_FORCE_PARAM) as Boolean
        }

        change_password_nav.nav_tvTitle.text = getString(R.string.change_password)
        change_password_nav.nav_imgBack.setOnClickListener {
            if (isForceChange) {
                Toast.makeText(this@ChangePasswordActivity, "Bạn cần thay đổi mật khẩu trước khi sử dụng.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            finish()
        }

        change_password_btn.setOnClickListener {
            changePassword()
        }

        initEditText()
        setGAScreenName("Change Password", GACategory.ACCOUNT)
    }

    override fun onBackPressed() {
        if (isForceChange) {
            return
        }
        super.onBackPressed()
    }

    private fun initEditText() {
        arrayOf(change_password_et_old, change_password_et_new,
                change_password_et_confirm).forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    checkEnableBtnChangePassword()
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                }
            })
        }
    }

    private fun checkEnableBtnChangePassword() {
        var enableBtn = true
        var textColorBtn = R.color.color_white
        arrayOf(change_password_et_old, change_password_et_new,
                change_password_et_confirm).forEach {
            if (it.text.isNullOrEmpty()) {
                enableBtn = false
                textColorBtn = R.color.color_green_btn_disable
            }
        }

        change_password_btn.setTextColor(Util.getColor(this, textColorBtn))
        change_password_btn.isEnabled = enableBtn
    }


    private fun changePassword() {
        val oldPassword = change_password_et_old.text.toString()
        val newPassword = change_password_et_new.text.toString()
        val confirmPassword = change_password_et_confirm.text.toString()

        if (newPassword != confirmPassword) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.change_password_error_not_match_title),
                    getString(R.string.change_password_error_not_match_desc),
                    getString(R.string.ok), null)
            return
        }

        LocalStorage.user()?.let {
            presenter.changePassword(oldPassword, newPassword, it)
        }
    }

    private fun onChangePasswordCompleted() {
        if (isForceChange) {
            MainActivity.start(this)
        }
        finish()
    }

    override fun showDialogChangePassFailed(message: String) {
        DialogUtil.showErrorDialog(this,
                getString(R.string.change_password_error_title),
                message,
                getString(R.string.ok), null)
    }

    override fun showDialogChangePassSuccess() {
        DialogUtil.showInfoDialog(this,
                getString(R.string.change_password_success_title),
                getString(R.string.change_password_success_desc),
                getString(R.string.ok),
                View.OnClickListener {
                    onChangePasswordCompleted()
                })
    }
}
