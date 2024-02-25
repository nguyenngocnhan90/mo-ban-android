package com.moban.flow.account.register

import com.moban.model.param.user.RegisterParam
import com.moban.mvp.BaseMvpPresenter

interface IRegisterPresenter: BaseMvpPresenter<IRegisterView> {
    fun confirmInfo(param: RegisterParam)
    fun loadSources()
}
