package com.moban.flow.signin

import com.moban.mvp.BaseMvpView

/**
 * Created by neal on 3/1/18.
 */

interface ISignInView : BaseMvpView {
    fun getInputUsername(): String
    fun getInputPassword(): String

    fun showInputNeededError()
    fun showLoginError(title: String, content: String)

    fun startMainScreen()

    fun openFacebookMessenger()

    fun startVerifyPhone()

    fun changePassword()
}
