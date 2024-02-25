package com.moban.flow.verifyphone

import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpPresenter

interface IVerifyPhonePresenter: BaseMvpPresenter<IVerifyPhoneView> {
    fun getOTP(user: User)
}
