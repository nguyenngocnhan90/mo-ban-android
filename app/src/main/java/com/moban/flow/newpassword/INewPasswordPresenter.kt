package com.moban.flow.newpassword

import com.moban.model.param.user.RecoverPasswordParam
import com.moban.mvp.BaseMvpPresenter

interface INewPasswordPresenter: BaseMvpPresenter<INewPasswordView> {
    fun recoverPassword(recoverPasswordParam: RecoverPasswordParam)
}
