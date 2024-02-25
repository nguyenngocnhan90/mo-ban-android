package com.moban.flow.account.changepassword

import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/13/18.
 */
interface IChangePasswordPresenter : BaseMvpPresenter<IChangePasswordView> {
    fun changePassword(oldPass: String, newPass: String, user: User)
}
