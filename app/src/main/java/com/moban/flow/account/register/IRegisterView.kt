package com.moban.flow.account.register

import com.moban.mvp.BaseMvpView

interface IRegisterView: BaseMvpView {
    fun gotSources(sources: List<String>)
    fun showConfirmRegisterSuccessful(message: String, otp: String)
    fun showConfirmRegisterFailed(message: String?)
}
