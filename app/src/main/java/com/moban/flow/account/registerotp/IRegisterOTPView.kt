package com.moban.flow.account.registerotp

import com.moban.mvp.BaseMvpView

interface IRegisterOTPView: BaseMvpView {
    fun showRegisterSuccessful()
    fun showRegisterFailed(message: String?)
}
