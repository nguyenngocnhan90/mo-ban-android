package com.moban.flow.forgotpassword

import com.moban.mvp.BaseMvpView

interface IForgotPasswordView: BaseMvpView {
    fun onResetPasswordCompleted()
    fun onResetPasswordFailed(error: String)
}
