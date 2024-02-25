package com.moban.flow.reward.badge

import com.moban.model.response.badge.ListBadges
import com.moban.mvp.BaseMvpView

interface IRewardBadgeView: BaseMvpView {
    fun bindingBadges(badges: ListBadges)
}
