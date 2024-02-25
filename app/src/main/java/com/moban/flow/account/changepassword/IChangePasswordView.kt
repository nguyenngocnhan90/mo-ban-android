package com.moban.flow.account.changepassword

import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/13/18.
 */
interface IChangePasswordView : BaseMvpView {
    fun showDialogChangePassFailed(message: String)
    fun showDialogChangePassSuccess()
}
