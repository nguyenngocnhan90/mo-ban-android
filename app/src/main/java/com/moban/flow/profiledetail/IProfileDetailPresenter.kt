package com.moban.flow.profiledetail

import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/10/18.
 */
interface IProfileDetailPresenter : BaseMvpPresenter<IProfileDetailView> {
    fun loadBadges(user: User)
}
