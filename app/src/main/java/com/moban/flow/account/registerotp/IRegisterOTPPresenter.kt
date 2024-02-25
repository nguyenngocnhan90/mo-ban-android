package com.moban.flow.account.registerotp

import com.moban.model.param.user.RegisterParam
import com.moban.mvp.BaseMvpPresenter

interface IRegisterOTPPresenter: BaseMvpPresenter<IRegisterOTPView> {
    fun register(param: RegisterParam)
}
