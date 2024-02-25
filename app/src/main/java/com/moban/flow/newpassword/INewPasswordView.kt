package com.moban.flow.newpassword

import com.moban.mvp.BaseMvpView

interface INewPasswordView: BaseMvpView {
    fun onRecoverPasswordCompleted()
    fun onRecoverPasswordFailed(title: String? = null, error: String)
}
