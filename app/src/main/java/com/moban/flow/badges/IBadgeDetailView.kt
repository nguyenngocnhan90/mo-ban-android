package com.moban.flow.badges

import com.moban.model.data.user.Badge
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/23/18.
 */
interface IBadgeDetailView : BaseMvpView {
    fun bindingBadgeDetail(badge: Badge)
}
