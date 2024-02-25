package com.moban.flow.more

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moban.LHApplication
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.AppType
import com.moban.flow.account.changepassword.ChangePasswordActivity
import com.moban.flow.cointransfer.CoinTransferActivity
import com.moban.flow.signin.SignInActivity
import com.moban.flow.splash.SplashActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.helper.LocalStorage
import com.moban.network.service.UserService
import com.moban.util.AppUtil
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import com.moban.util.Permission
import kotlinx.android.synthetic.main.fragment_more.*

class MoreFragment : Fragment() {

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var dialog: Dialog? = null
    private lateinit var fragmentActivity: FragmentActivity
    private val isOutSide = LocalStorage.user()?.isOutsideAgent ?: false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentActivity = this.activity!!

        initCollapsibleView()

        more_item_logout.setOnClickListener {
            showDialogLogout()
        }

        more_item_change_password.setOnClickListener {
            showChangePassword()
        }

        more_item_live_chat.setOnClickListener {
            openFacebookMessenger()
        }

        more_item_quick_ask.setOnClickListener {
            openFAQ()
        }

        if (isOutSide) {
            more_item_transfer_lhc.alpha = 0.5F
        }

        more_item_transfer_lhc.setOnClickListener {
            if (!isOutSide) {
                transferLHC()
            }
        }

        more_btn_login.setOnClickListener {
            SignInActivity.start(fragmentActivity, fragmentActivity)
        }

        context?.let {
            val phone = getString(R.string.phone_support)
            val supportCenter = getString(R.string.call_hotline) + " " + phone

            more_item_tv_support_center.text = supportCenter

            more_item_quick_ask.visibility = if (AppUtil.getAppType(it) == AppType.LINKHOUSE) View.VISIBLE else
                View.GONE

            more_item_call_hotline.setOnClickListener {
                Permission.openCalling(fragmentActivity, phone)
            }
        }

        val isAnonymous = LocalStorage.user()?.isAnonymous() ?: false
        if (isAnonymous) {
            more_view_login.visibility = View.VISIBLE
            more_section_settings.visibility = View.GONE
            more_item_security.visibility = View.GONE
            more_item_logout.visibility = View.GONE
        }
    }

    private fun transferLHC() {

        CoinTransferActivity.start(fragmentActivity)
    }

    private fun openFAQ() {
        WebViewActivity.start(fragmentActivity, getString(R.string.faq), Constant.LINKHOUSE_FAQ)
    }

    private fun openFacebookMessenger() {
        context?.let {
            val fanpageId = Constant.FACEBOOK_FANPAGE_ID
            AppUtil.openFacebookMessenger(it, fanpageId)
        }
    }

    private fun initCollapsibleView() {
        val headerItemArr = arrayOf(more_item_personal_info, more_item_security)
        val contentItemArr = arrayOf(more_item_social, more_item_security_content)
        val arrowImageArr = arrayOf(more_item_img_personal_info, more_item_img_security)

        headerItemArr.forEachIndexed { index, linearLayout ->
            linearLayout.setOnClickListener {
                contentItemArr[index].visibility = if (contentItemArr[index].visibility == View.VISIBLE)
                    View.GONE else View.VISIBLE

                val arrowIcon = if (contentItemArr[index].visibility == View.VISIBLE) R.drawable.arrow_up
                else R.drawable.arrow_down
                arrowImageArr[index].setImageResource(arrowIcon)
            }
        }
    }

    private fun showChangePassword() {
        ChangePasswordActivity.start(fragmentActivity)
    }

    private fun showDialogLogout() {
        dialog = DialogUtil.showConfirmDialog(fragmentActivity, true,
                R.string.logout, R.string.more_logout_question,
                R.string.logout, R.string.skip,
                View.OnClickListener {
                    dismissDialog()
                    logout()
                }, null)
    }

    private fun logout() {
        if (!NetworkUtil.hasConnection(fragmentActivity)) {
            dialog = DialogUtil.showNetworkError(fragmentActivity, View.OnClickListener {
                dismissDialog()
                logout()
            })
            return
        }

        userService?.logout()
        deleteUserInfo()
        backToLogin()
    }

    private fun deleteUserInfo() {
        LocalStorage.saveToken(null)
        LocalStorage.saveUser(null)
        LocalStorage.saveUnReadNotification(0)
        LHApplication.instance.lhCache.popup = null
        LHApplication.instance.lhCache.oneTimeToken = ""
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun backToLogin() {
        SplashActivity.start(fragmentActivity)
    }

}
