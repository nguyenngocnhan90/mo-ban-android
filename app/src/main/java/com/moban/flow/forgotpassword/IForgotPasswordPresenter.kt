package com.moban.flow.forgotpassword

import com.moban.mvp.BaseMvpPresenter

interface IForgotPasswordPresenter: BaseMvpPresenter<IForgotPasswordView> {
    fun resetPassword(userName: String)
}
