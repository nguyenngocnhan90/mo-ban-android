package com.moban.flow.account.register

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.moban.R
import com.moban.adapter.register.RegisterSourceAdapter
import com.moban.enum.GACategory
import com.moban.extension.isValidEmail
import com.moban.extension.isValidPhoneNumber
import com.moban.flow.account.registerotp.RegisterOTPActivity
import com.moban.handler.PartnerItemHandler
import com.moban.model.data.user.User
import com.moban.model.param.user.RegisterParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import com.moban.view.custom.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.item_interests_group.view.*
import kotlinx.android.synthetic.main.nav.view.*

class RegisterActivity : BaseMvpActivity<IRegisterView, IRegisterPresenter>(), IRegisterView {
    override var presenter: IRegisterPresenter = RegisterPresenterIml()
    private var referer: User? = null
    private lateinit var param: RegisterParam

    private var sourceAdapter = RegisterSourceAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        register_nav.nav_tvTitle.text = getString(R.string.register_title)
        register_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        initSelectDialog()
        register_btn_register.setOnClickListener {
            submitRegister()
        }
        setGAScreenName("Register Account", GACategory.ACCOUNT)

        initRecyclerViewSource()

        // Get sources
        presenter.loadSources()
    }

    private fun initRecyclerViewSource() {
        val layoutManager = CustomLinearLayoutManager(this)
        register_recycler_view_sources.layoutManager = layoutManager
        register_recycler_view_sources.adapter = sourceAdapter
    }

    private fun submitRegister() {
        val name = register_ed_fullname.text.toString()
        val email = register_ed_email.text.toString()
        val phone = register_ed_phone.text.toString()

        if (name.isEmpty()) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_name_empty),
                    getString(R.string.error_register_name_empty_desc),
                    getString(R.string.ok), null)
            return
        }

        if (!email.isValidEmail()) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_email_invalid),
                    getString(R.string.error_register_email_invalid_desc),
                    getString(R.string.ok), null)
            return
        }

        if (!phone.isValidPhoneNumber()) {
            DialogUtil.showErrorDialog(this,
                    getString(R.string.error_register_phone_invalid),
                    getString(R.string.error_register_phone_invalid_desc),
                    getString(R.string.ok), null)
            return
        }

        param = RegisterParam()
        param.name = name
        param.email = email
        param.phone = phone
        param.source = sourceAdapter.selectedSource

        referer?.let {
            param.ref_id = it.id
            param.ref_name = it.name
        }

        register_progressbar.visibility = View.VISIBLE
        presenter.confirmInfo(param)
    }

    private fun initSelectDialog() {
        register_view_referer.setOnClickListener {
            val dialog = DialogSearchPartner(this, this, object: PartnerItemHandler {
                override fun onClicked(user: User) {
                    referer = user
                    register_tv_referer.text = user.name
                    register_tv_referer.setTextColor(StringUtil.getColor(this@RegisterActivity, R.color.color_black))
                }
            })

            dialog.showDialog()
        }
    }

    override fun gotSources(sources: List<String>) {
        sourceAdapter.sources = sources
        sourceAdapter.notifyDataSetChanged()
    }

    override fun showConfirmRegisterFailed(message: String?) {
        register_progressbar.visibility = View.GONE
        val error = if (!message.isNullOrEmpty()) message else getString(R.string.error_register_failed)
        DialogUtil.showErrorDialog(this, "", error, getString(R.string.ok), null)
    }

    override fun showConfirmRegisterSuccessful(message: String, otp: String) {
        register_progressbar.visibility = View.GONE
        var dialog: Dialog? = null
        dialog = DialogUtil.showInfoDialog(this, getString(R.string.register_congrats_title), message,
                getString(R.string.ok), View.OnClickListener {
                doOkAction(otp)
                dialog?.dismiss()
        })
    }

    private fun doOkAction(otp: String) {
        param.otp = otp
        RegisterOTPActivity.start(this, param)
    }
}
