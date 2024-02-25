package com.moban.flow.splash

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.moban.LHApplication
import com.moban.R
import com.moban.extension.getString
import com.moban.flow.main.MainActivity
import com.moban.flow.signin.SignInActivity
import com.moban.helper.LocalStorage
import com.moban.model.param.user.UpdateDeviceTokenParam
import com.moban.model.response.ForceResponse
import com.moban.model.response.LoginResponse
import com.moban.network.service.DeviceService
import com.moban.network.service.UserService
import com.moban.util.Device
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            context.startActivity(intent)
        }
    }

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)
    private val deviceService = retrofit?.create(DeviceService::class.java)

    private var networkDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()

        Handler()
                .postDelayed({
                    if (!isFinishing) {
                        getVersion()
                    }
                }, 1500)
    }

    private fun getVersion() {
        if (!NetworkUtil.hasConnection(this)) {
            networkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dismissNetworkDialog()
                getVersion()
            })

            return
        }

        val currentBuild = Device.getVersionCode(this)
        deviceService?.getLatestVersion(currentBuild)?.enqueue(object : Callback<ForceResponse> {
            override fun onFailure(call: Call<ForceResponse>?, t: Throwable?) {
                checkUserProfile()
            }

            override fun onResponse(call: Call<ForceResponse>?, response: Response<ForceResponse>?) {
                val body = response?.body()
                if (body == null) {
                    checkUserProfile()
                    return
                }

                processGetLatestVersionResponse(body, currentBuild)
            }
        }
        )
    }

    private fun processGetLatestVersionResponse(forceResponse: ForceResponse, currentBuild: Int) {
        if (forceResponse.success) {
            forceResponse.data?.let {
                val hasUpdate: Boolean = currentBuild < it.latest_version

                if (hasUpdate) {
                    showDialogUpdateApp(it.force)
                    return
                }
            }
        }
        checkUserProfile()
    }

    private fun showDialogUpdateApp(forceUpdate: Boolean) {
        var updateDialog: Dialog? = null
        if (forceUpdate) {
            updateDialog = DialogUtil.showInfoDialog(this, getString(this, R.string.update_ohio),
                    getString(this, R.string.update_ohio_force_new_version), getString(R.string.update),
                    View.OnClickListener {
                        openGooglePlay()
                    })
        } else {
            updateDialog = DialogUtil.showConfirmDialog(this, false, getString(this, R.string.update_ohio),
                    getString(this, R.string.update_ohio_new_version), getString(R.string.update), getString(R.string.skip),
                    View.OnClickListener {
                        openGooglePlay()
                    },
                    View.OnClickListener {
                        updateDialog?.dismiss()
                        checkUserProfile()
                    })
        }
    }

    private fun openGooglePlay() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=" + Device.getPackageName(this)))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun checkUserProfile() {
        val user = LocalStorage.user()
        val token = LocalStorage.token()
        if (user != null && !token.isNullOrBlank() && !user.isAnonymous()) {
            getProfile()
        } else {
            loginAnonymous()
        }
    }

    private fun getProfile() {
        if (!NetworkUtil.hasConnection(this)) {
            if (!this.isFinishing) {
                networkDialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                    dismissNetworkDialog()
                    getProfile()
                })
            }
            return
        }

        userService?.profile()?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                loginAnonymous()
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { user ->
                            LocalStorage.saveUser(user)
                            if (!user.token.isNullOrBlank()) {
                                LocalStorage.saveToken(user.token)
                            }
                            if (!user.linkmart_token.isNullOrBlank()) {
                                LHApplication.instance.initLinkmartNetComponent()
                            }
                            LHApplication.instance.lhCache.popup = user.popup
                        }

                        updateDeviceToken()
                        startMainActivity()
                    } else {
                        loginAnonymous()
                    }
                }
            }
        })
    }

    private fun loginAnonymous() {
        userService?.loginAnonymous()?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                startSignInActivity()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { user ->
                            LocalStorage.saveUser(user)
                            if (!user.token.isNullOrBlank()) {
                                LocalStorage.saveToken(user.token)
                            }
                            if (!user.linkmart_token.isNullOrBlank()) {
                                LHApplication.instance.initLinkmartNetComponent()
                            }
                            LHApplication.instance.lhCache.popup = user.popup
                            LHApplication.instance.initNetComponent()
                            LHApplication.instance.initLinkmartNetComponent()
                        }

                        updateDeviceToken()
                        startMainActivity()
                    } else {
                        startSignInActivity()
                    }
                } ?: run {
                    startSignInActivity()
                }
            }
        })
    }

    private fun updateDeviceToken() {
        if (!NetworkUtil.hasConnection(this)) {
            return
        }

        deviceService?.updateDeviceToken(UpdateDeviceTokenParam())
    }

    private fun startSignInActivity() {
        SignInActivity.start(this)
        finish()
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        finish()
    }

    private fun dismissNetworkDialog() {
        networkDialog?.dismiss()
    }
}
