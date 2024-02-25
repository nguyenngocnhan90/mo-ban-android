package com.moban.flow.reward.partner

import com.moban.model.data.user.LevelGift
import com.moban.mvp.BaseMvpPresenter

interface IRewardPartnerPresenter: BaseMvpPresenter<IRewardPartnerView> {
    fun markUseGift(gift: LevelGift)
    fun buyLinkmart(gift: LevelGift)
}
