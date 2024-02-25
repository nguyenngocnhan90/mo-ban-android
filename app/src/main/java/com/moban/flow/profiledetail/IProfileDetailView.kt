package com.moban.flow.profiledetail

import com.moban.model.data.user.Badge
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/10/18.
 */
interface IProfileDetailView : BaseMvpView {
    fun bindingBadges(badges: List<Badge>)
}
