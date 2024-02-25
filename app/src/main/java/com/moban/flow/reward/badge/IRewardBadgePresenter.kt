package com.moban.flow.reward.badge

import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpPresenter

interface IRewardBadgePresenter: BaseMvpPresenter<IRewardBadgeView> {
    fun loadBadges(user: User)
}
