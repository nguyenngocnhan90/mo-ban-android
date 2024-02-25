package com.moban.flow.verifyphone

import com.moban.mvp.BaseMvpView

interface IVerifyPhoneView: BaseMvpView {
    fun showErrorGetOTP()
    fun getOTPCompleted()

    fun verifyPhoneCompleted()
    fun verifyPhoneFailed(error: String?)
}
