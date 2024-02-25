package com.moban.flow.signin

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by neal on 3/1/18.
 */
interface ISignInPresenter : BaseMvpPresenter<ISignInView> {
    fun handleSignIn()
    fun loginAnonymous()
}
