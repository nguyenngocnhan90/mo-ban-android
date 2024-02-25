package com.moban.flow.badges

import com.moban.model.data.user.Badge
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/23/18.
 */
interface IBadgeDetailPresenter : BaseMvpPresenter<IBadgeDetailView> {
    fun loadBadgeDetail(badge: Badge)
}
